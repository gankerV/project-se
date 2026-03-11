package com.erplite.inventory.service;

import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.lot.*;
import com.erplite.inventory.dto.transaction.TransactionResponse;
import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import com.erplite.inventory.entity.InventoryTransaction;
import com.erplite.inventory.entity.InventoryTransaction.TransactionType;
import com.erplite.inventory.entity.Material;
import com.erplite.inventory.exception.BusinessException;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.InventoryLotRepository;
import com.erplite.inventory.repository.InventoryTransactionRepository;
import com.erplite.inventory.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryLotService {

    private final InventoryLotRepository lotRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final MaterialRepository materialRepository;

    public PagedResponse<LotResponse> listLots(String materialId, LotStatus status,
            Boolean nearExpiry, Boolean isSample, Pageable pageable) {
        Page<InventoryLot> page;
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(30);

        if (Boolean.TRUE.equals(nearExpiry)) {
            page = lotRepository.findNearExpiry(today, cutoff, pageable);
        } else if (Boolean.TRUE.equals(isSample)) {
            page = lotRepository.findByIsSample(true, pageable);
        } else if (materialId != null && !materialId.isBlank() && status != null) {
            page = lotRepository.findByMaterial_MaterialIdAndStatus(materialId, status, pageable);
        } else if (materialId != null && !materialId.isBlank()) {
            page = lotRepository.findByMaterial_MaterialId(materialId, pageable);
        } else if (status != null) {
            page = lotRepository.findByStatus(status, pageable);
        } else {
            page = lotRepository.findAll(pageable);
        }
        return PagedResponse.from(page.map(LotResponse::from));
    }

    public LotResponse getLotById(String id) {
        return LotResponse.from(findLotOrThrow(id));
    }

    @Transactional
    public LotResponse receiveLot(LotReceiveRequest req) {
        Material material = materialRepository.findById(req.getMaterialId())
            .orElseThrow(() -> new ResourceNotFoundException("Material", "id", req.getMaterialId()));

        InventoryLot lot = InventoryLot.builder()
            .material(material)
            .manufacturerName(req.getManufacturerName())
            .manufacturerLot(req.getManufacturerLot())
            .supplierName(req.getSupplierName())
            .quantity(req.getQuantity())
            .unitOfMeasure(req.getUnitOfMeasure())
            .status(LotStatus.Quarantine)
            .receivedDate(req.getReceivedDate() != null ? req.getReceivedDate() : LocalDate.now())
            .expirationDate(req.getExpirationDate())
            .inUseExpirationDate(req.getInUseExpirationDate())
            .storageLocation(req.getStorageLocation())
            .poNumber(req.getPoNumber())
            .receivingFormId(req.getReceivingFormId())
            .isSample(false)
            .build();

        lot = lotRepository.save(lot);
        recordTransaction(lot, TransactionType.Receipt, req.getQuantity(),
            null, "Initial stock receipt", req.getPerformedBy());
        return LotResponse.from(lot);
    }

    @Transactional
    public LotResponse updateLotStatus(String id, LotStatusUpdateRequest req) {
        InventoryLot lot = findLotOrThrow(id);
        validateStatusTransition(lot.getStatus(), req.getStatus());
        LotStatus oldStatus = lot.getStatus();
        lot.setStatus(req.getStatus());
        lot = lotRepository.save(lot);
        String notes = req.getNotes() != null ? req.getNotes()
            : String.format("Status changed: %s → %s", oldStatus, req.getStatus());
        recordTransaction(lot, TransactionType.Adjustment, BigDecimal.ZERO, null, notes, req.getPerformedBy());
        return LotResponse.from(lot);
    }

    @Transactional
    public LotResponse splitLot(String id, LotSplitRequest req) {
        InventoryLot parent = findLotOrThrow(id);
        if (parent.getStatus() != LotStatus.Accepted) {
            throw new BusinessException("Can only split lots with Accepted status");
        }
        if (parent.getQuantity().compareTo(req.getSampleQuantity()) < 0) {
            throw new BusinessException("Insufficient quantity for split. Available: " + parent.getQuantity());
        }

        parent.setQuantity(parent.getQuantity().subtract(req.getSampleQuantity()));
        parent = lotRepository.save(parent);
        recordTransaction(parent, TransactionType.Split, req.getSampleQuantity().negate(),
            null, "Split: sample removed", req.getPerformedBy());

        InventoryLot sample = InventoryLot.builder()
            .material(parent.getMaterial())
            .manufacturerName(parent.getManufacturerName())
            .manufacturerLot(parent.getManufacturerLot())
            .supplierName(parent.getSupplierName())
            .quantity(req.getSampleQuantity())
            .unitOfMeasure(parent.getUnitOfMeasure())
            .status(LotStatus.Quarantine)
            .receivedDate(parent.getReceivedDate())
            .expirationDate(parent.getExpirationDate())
            .inUseExpirationDate(parent.getInUseExpirationDate())
            .storageLocation(req.getStorageLocation() != null ? req.getStorageLocation() : parent.getStorageLocation())
            .isSample(true)
            .parentLot(parent)
            .build();

        sample = lotRepository.save(sample);
        recordTransaction(sample, TransactionType.Split, req.getSampleQuantity(),
            parent.getLotId(), "Split from parent lot", req.getPerformedBy());
        return LotResponse.from(sample);
    }

    @Transactional
    public LotResponse adjustLot(String id, LotAdjustRequest req) {
        InventoryLot lot = findLotOrThrow(id);
        BigDecimal newQuantity = lot.getQuantity().add(req.getAdjustmentQuantity());
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Adjustment would result in negative quantity");
        }
        lot.setQuantity(newQuantity);
        lot = lotRepository.save(lot);
        recordTransaction(lot, TransactionType.Adjustment, req.getAdjustmentQuantity(),
            null, req.getReason(), req.getPerformedBy());
        return LotResponse.from(lot);
    }

    @Transactional
    public LotResponse transferLot(String id, LotTransferRequest req) {
        InventoryLot lot = findLotOrThrow(id);
        String oldLocation = lot.getStorageLocation();
        lot.setStorageLocation(req.getNewStorageLocation());
        lot = lotRepository.save(lot);
        String notes = req.getNotes() != null ? req.getNotes()
            : String.format("Transferred from %s to %s", oldLocation, req.getNewStorageLocation());
        recordTransaction(lot, TransactionType.Transfer, BigDecimal.ZERO, null, notes, req.getPerformedBy());
        return LotResponse.from(lot);
    }

    @Transactional
    public LotResponse disposeLot(String id, LotDisposeRequest req) {
        InventoryLot lot = findLotOrThrow(id);
        if (lot.getQuantity().compareTo(req.getDisposalQuantity()) < 0) {
            throw new BusinessException("Insufficient quantity for disposal. Available: " + lot.getQuantity());
        }
        lot.setQuantity(lot.getQuantity().subtract(req.getDisposalQuantity()));
        if (lot.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            lot.setStatus(LotStatus.Depleted);
        }
        lot = lotRepository.save(lot);
        recordTransaction(lot, TransactionType.Disposal, req.getDisposalQuantity().negate(),
            null, req.getReason(), req.getPerformedBy());
        return LotResponse.from(lot);
    }

    public List<TransactionResponse> getTransactions(String lotId, String type) {
        findLotOrThrow(lotId);
        List<InventoryTransaction> txList = transactionRepository.findByLot_LotIdOrderByTransactionDateDesc(lotId);
        if (type != null && !type.isBlank()) {
            String upperType = type.toUpperCase();
            txList = txList.stream()
                .filter(tx -> tx.getTransactionType() != null &&
                    tx.getTransactionType().name().equalsIgnoreCase(upperType))
                .collect(Collectors.toList());
        }
        return txList.stream().map(TransactionResponse::from).collect(Collectors.toList());
    }

    private InventoryLot findLotOrThrow(String id) {
        return lotRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", id));
    }

    private void validateStatusTransition(LotStatus current, LotStatus next) {
        boolean valid = switch (current) {
            case Quarantine -> next == LotStatus.Accepted || next == LotStatus.Rejected;
            case Accepted -> next == LotStatus.Depleted;
            case Rejected -> next == LotStatus.Depleted;
            case Depleted -> false;
        };
        if (!valid) {
            throw new BusinessException(String.format("Invalid status transition: %s → %s", current, next));
        }
    }

    private void recordTransaction(InventoryLot lot, TransactionType type, BigDecimal quantity,
            String referenceId, String notes, String performedBy) {
        InventoryTransaction tx = InventoryTransaction.builder()
            .lot(lot)
            .transactionType(type)
            .quantity(quantity)
            .unitOfMeasure(lot.getUnitOfMeasure())
            .referenceId(referenceId)
            .notes(notes)
            .performedBy(performedBy)
            .build();
        transactionRepository.save(tx);
    }
}

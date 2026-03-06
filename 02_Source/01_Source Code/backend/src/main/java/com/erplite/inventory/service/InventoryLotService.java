package com.erplite.inventory.service;

import com.erplite.inventory.dto.InventoryLotRequestDTO;
import com.erplite.inventory.dto.InventoryLotResponseDTO;
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

    /**
     * Lấy danh sách lot, hỗ trợ filter theo materialId và/hoặc status.
     */
    public List<InventoryLotResponseDTO> getLots(String materialId, LotStatus status) {
        List<InventoryLot> lots;

        if (materialId != null && !materialId.isBlank() && status != null) {
            lots = lotRepository.findByMaterial_MaterialIdAndStatus(materialId, status);
        } else if (materialId != null && !materialId.isBlank()) {
            lots = lotRepository.findByMaterial_MaterialId(materialId);
        } else if (status != null) {
            lots = lotRepository.findByStatus(status);
        } else {
            lots = lotRepository.findAll();
        }

        return lots.stream()
                .map(InventoryLotResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết lot theo ID.
     */
    public InventoryLotResponseDTO getLotById(String id) {
        return InventoryLotResponseDTO.fromEntity(findLotOrThrow(id));
    }

    /**
     * Nhập kho: tạo lot mới (status = Quarantine) và ghi Receipt transaction.
     */
    @Transactional
    public InventoryLotResponseDTO receiveNewLot(InventoryLotRequestDTO dto) {
        Material material = materialRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", dto.getMaterialId()));

        InventoryLot lot = InventoryLot.builder()
                .material(material)
                .manufacturerLot(dto.getManufacturerLot())
                .quantity(dto.getQuantity())
                .unitOfMeasure(dto.getUnitOfMeasure())
                .status(LotStatus.Quarantine)
                .receivedDate(dto.getReceivedDate() != null ? dto.getReceivedDate() : LocalDate.now())
                .expirationDate(dto.getExpirationDate())
                .storageLocation(dto.getStorageLocation())
                .isSample(false)
                .build();

        lot = lotRepository.save(lot);

        // Ghi Receipt transaction
        recordTransaction(lot, TransactionType.Receipt, dto.getQuantity(),
                null, "Initial stock receipt", dto.getPerformedBy());

        return InventoryLotResponseDTO.fromEntity(lot);
    }

    /**
     * Cập nhật trạng thái lot.
     * Các chuyển đổi được phép:
     * Quarantine → Accepted | Rejected
     * Accepted → Depleted (thường tự động, nhưng cho phép manual)
     * Rejected → Depleted
     */
    @Transactional
    public InventoryLotResponseDTO updateLotStatus(String id, LotStatus newStatus, String performedBy) {
        InventoryLot lot = findLotOrThrow(id);

        validateStatusTransition(lot.getStatus(), newStatus);

        LotStatus oldStatus = lot.getStatus();
        lot.setStatus(newStatus);
        lot = lotRepository.save(lot);

        // Ghi Adjustment transaction để có audit trail
        String notes = String.format("Status changed: %s → %s", oldStatus, newStatus);
        recordTransaction(lot, TransactionType.Adjustment, BigDecimal.ZERO, null, notes, performedBy);

        return InventoryLotResponseDTO.fromEntity(lot);
    }

    /**
     * Lấy lịch sử giao dịch của một lot.
     */
    public List<InventoryTransaction> getLotTransactions(String lotId) {
        findLotOrThrow(lotId); // validate lot exists
        return transactionRepository.findByLot_LotIdOrderByTransactionDateDesc(lotId);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private InventoryLot findLotOrThrow(String id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", id));
    }

    private void validateStatusTransition(LotStatus current, LotStatus next) {
        boolean valid = switch (current) {
            case Quarantine -> next == LotStatus.Accepted || next == LotStatus.Rejected;
            case Accepted -> next == LotStatus.Depleted;
            case Rejected -> next == LotStatus.Depleted;
            case Depleted -> false; // Terminal state
        };

        if (!valid) {
            throw new BusinessException(
                    String.format("Invalid status transition: %s → %s", current, next));
        }
    }

    private void recordTransaction(InventoryLot lot, TransactionType type, BigDecimal quantity,
            String referenceId, String notes, String performedBy) {
        InventoryTransaction tx = InventoryTransaction.builder()
                .lot(lot)
                .transactionType(type)
                .quantity(quantity)
                .referenceId(referenceId)
                .notes(notes)
                .performedBy(performedBy)
                .build();
        transactionRepository.save(tx);
    }
}

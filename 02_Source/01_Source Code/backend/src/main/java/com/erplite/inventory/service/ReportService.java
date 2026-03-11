package com.erplite.inventory.service;

import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.lot.LotResponse;
import com.erplite.inventory.dto.report.*;
import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import com.erplite.inventory.entity.InventoryTransaction;
import com.erplite.inventory.entity.Material.MaterialType;
import com.erplite.inventory.entity.ProductionBatch.BatchStatus;
import com.erplite.inventory.entity.QCTest;
import com.erplite.inventory.entity.QCTest.ResultStatus;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final MaterialRepository materialRepository;
    private final InventoryLotRepository lotRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final QCTestRepository qcTestRepository;
    private final ProductionBatchRepository batchRepository;

    public DashboardResponse getDashboard() {
        long totalMaterials = materialRepository.count();
        List<InventoryLot> allLots = lotRepository.findAll();
        long totalActiveLots = allLots.stream()
            .filter(l -> l.getStatus() == LotStatus.Quarantine || l.getStatus() == LotStatus.Accepted)
            .count();

        Map<String, Long> byStatus = allLots.stream()
            .filter(l -> l.getStatus() != null)
            .collect(Collectors.groupingBy(l -> l.getStatus().name(), Collectors.counting()));

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(30);
        long nearExpiryLots = lotRepository.findNearExpiry(today, cutoff).size();
        long activeBatches = batchRepository.findByStatus(BatchStatus.IN_PROGRESS).size();

        LocalDate thirtyDaysAgo = today.minusDays(30);
        long failedQCLast30Days = qcTestRepository.findByTestDateBetween(thirtyDaysAgo, today)
            .stream().filter(t -> t.getResultStatus() == ResultStatus.Fail).count();

        return DashboardResponse.builder()
            .totalMaterials(totalMaterials)
            .totalActiveLots(totalActiveLots)
            .byStatus(byStatus)
            .nearExpiryLots(nearExpiryLots)
            .activeBatches(activeBatches)
            .failedQCLast30Days(failedQCLast30Days)
            .asOf(LocalDateTime.now())
            .build();
    }

    public PagedResponse<NearExpiryItemResponse> getNearExpiry(int days, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(days);
        Page<InventoryLot> page = lotRepository.findNearExpiry(today, cutoff, pageable);
        return PagedResponse.from(page.map(NearExpiryItemResponse::from));
    }

    public LotTraceResponse getLotTrace(String lotId) {
        InventoryLot lot = lotRepository.findById(lotId)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", lotId));

        List<InventoryTransaction> txList =
            transactionRepository.findByLot_LotIdOrderByTransactionDateDesc(lotId);
        List<LotTraceResponse.TransactionSummary> txSummaries = txList.stream()
            .map(tx -> LotTraceResponse.TransactionSummary.builder()
                .transactionId(tx.getTransactionId())
                .transactionType(tx.getTransactionType() != null ? tx.getTransactionType().name() : null)
                .quantity(tx.getQuantity())
                .transactionDate(tx.getTransactionDate())
                .performedBy(tx.getPerformedBy())
                .notes(tx.getNotes())
                .build())
            .collect(Collectors.toList());

        List<QCTest> qcTests = qcTestRepository.findByLot_LotIdOrderByTestDateDesc(lotId);
        List<LotTraceResponse.QCTestSummary> qcSummaries = qcTests.stream()
            .map(t -> LotTraceResponse.QCTestSummary.builder()
                .testId(t.getTestId())
                .testType(t.getTestType() != null ? t.getTestType().name() : null)
                .testDate(t.getTestDate())
                .resultStatus(t.getResultStatus() != null ? t.getResultStatus().name() : null)
                .performedBy(t.getPerformedBy())
                .build())
            .collect(Collectors.toList());

        List<LotTraceResponse.BatchUsage> batchUsages = txList.stream()
            .filter(tx -> tx.getTransactionType() != null &&
                tx.getTransactionType() == com.erplite.inventory.entity.InventoryTransaction.TransactionType.Usage &&
                tx.getReferenceId() != null)
            .map(tx -> LotTraceResponse.BatchUsage.builder()
                .batchId(tx.getReferenceId())
                .batchNumber(null)
                .usedQuantity(tx.getQuantity() != null ? tx.getQuantity().abs() : null)
                .usedAt(tx.getTransactionDate())
                .build())
            .collect(Collectors.toList());

        return LotTraceResponse.builder()
            .lot(LotResponse.from(lot))
            .transactions(txSummaries)
            .qcTests(qcSummaries)
            .batchUsages(batchUsages)
            .build();
    }

    public QCReportResponse getQCReport(LocalDate from, LocalDate to) {
        List<QCTest> tests = qcTestRepository.findByTestDateBetween(from, to);
        long passed = tests.stream().filter(t -> t.getResultStatus() == ResultStatus.Pass).count();
        long failed = tests.stream().filter(t -> t.getResultStatus() == ResultStatus.Fail).count();
        long pending = tests.stream().filter(t -> t.getResultStatus() == ResultStatus.Pending).count();
        double passRate = tests.isEmpty() ? 0.0 : (double) passed / tests.size() * 100;

        Map<String, List<QCTest>> byMaterial = tests.stream()
            .filter(t -> t.getLot() != null && t.getLot().getMaterial() != null)
            .collect(Collectors.groupingBy(t -> t.getLot().getMaterial().getMaterialId()));

        List<QCReportResponse.MaterialQCSummary> materialSummaries = byMaterial.entrySet().stream()
            .map(e -> {
                List<QCTest> matTests = e.getValue();
                QCTest first = matTests.get(0);
                return QCReportResponse.MaterialQCSummary.builder()
                    .materialId(e.getKey())
                    .materialName(first.getLot().getMaterial().getMaterialName())
                    .partNumber(first.getLot().getMaterial().getPartNumber())
                    .totalTests(matTests.size())
                    .passed(matTests.stream().filter(t -> t.getResultStatus() == ResultStatus.Pass).count())
                    .failed(matTests.stream().filter(t -> t.getResultStatus() == ResultStatus.Fail).count())
                    .pending(matTests.stream().filter(t -> t.getResultStatus() == ResultStatus.Pending).count())
                    .build();
            })
            .collect(Collectors.toList());

        return QCReportResponse.builder()
            .period(QCReportResponse.Period.builder().from(from).to(to).build())
            .summary(QCReportResponse.Summary.builder()
                .totalTests(tests.size())
                .passed(passed).failed(failed).pending(pending)
                .passRate(passRate)
                .build())
            .byMaterial(materialSummaries)
            .build();
    }

    public List<InventorySnapshotResponse> getInventorySnapshot(LotStatus status, MaterialType materialType) {
        List<InventoryLot> lots = lotRepository.findAll();

        if (status != null) {
            lots = lots.stream().filter(l -> l.getStatus() == status).collect(Collectors.toList());
        }
        if (materialType != null) {
            lots = lots.stream()
                .filter(l -> l.getMaterial() != null && l.getMaterial().getMaterialType() == materialType)
                .collect(Collectors.toList());
        }

        Map<String, List<InventoryLot>> byMaterial = lots.stream()
            .filter(l -> l.getMaterial() != null)
            .collect(Collectors.groupingBy(l -> l.getMaterial().getMaterialId()));

        return byMaterial.entrySet().stream().map(e -> {
            List<InventoryLot> matLots = e.getValue();
            var material = matLots.get(0).getMaterial();

            Map<String, InventorySnapshotResponse.LotCountQty> lotsMap = new HashMap<>();
            for (LotStatus s : LotStatus.values()) {
                List<InventoryLot> filtered = matLots.stream()
                    .filter(l -> l.getStatus() == s).collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    BigDecimal total = filtered.stream()
                        .map(l -> l.getQuantity() != null ? l.getQuantity() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    lotsMap.put(s.name(), InventorySnapshotResponse.LotCountQty.builder()
                        .count(filtered.size()).totalQuantity(total).build());
                }
            }

            List<InventoryLot> acceptedLots = matLots.stream()
                .filter(l -> l.getStatus() == LotStatus.Accepted).collect(Collectors.toList());
            BigDecimal totalAvailable = acceptedLots.stream()
                .map(l -> l.getQuantity() != null ? l.getQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            String unit = matLots.stream().map(InventoryLot::getUnitOfMeasure)
                .filter(Objects::nonNull).findFirst().orElse(null);

            return InventorySnapshotResponse.builder()
                .materialId(e.getKey())
                .partNumber(material.getPartNumber())
                .materialName(material.getMaterialName())
                .materialType(material.getMaterialType() != null ? material.getMaterialType().name() : null)
                .lots(lotsMap)
                .totalAvailable(totalAvailable)
                .unit(unit)
                .build();
        }).collect(Collectors.toList());
    }
}

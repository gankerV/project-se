package com.erplite.inventory.dto.report;

import com.erplite.inventory.dto.lot.LotResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LotTraceResponse {
    private LotResponse lot;
    private List<TransactionSummary> transactions;
    private List<QCTestSummary> qcTests;
    private List<BatchUsage> batchUsages;

    @Data
    @Builder
    public static class TransactionSummary {
        private String transactionId;
        private String transactionType;
        private BigDecimal quantity;
        private LocalDateTime transactionDate;
        private String performedBy;
        private String notes;
    }

    @Data
    @Builder
    public static class QCTestSummary {
        private String testId;
        private String testType;
        private LocalDate testDate;
        private String resultStatus;
        private String performedBy;
    }

    @Data
    @Builder
    public static class BatchUsage {
        private String batchId;
        private String batchNumber;
        private BigDecimal usedQuantity;
        private LocalDateTime usedAt;
    }
}

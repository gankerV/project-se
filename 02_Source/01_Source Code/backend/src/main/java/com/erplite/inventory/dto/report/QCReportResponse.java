package com.erplite.inventory.dto.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class QCReportResponse {
    private Period period;
    private Summary summary;
    private List<MaterialQCSummary> byMaterial;

    @Data
    @Builder
    public static class Period {
        private LocalDate from;
        private LocalDate to;
    }

    @Data
    @Builder
    public static class Summary {
        private long totalTests;
        private long passed;
        private long failed;
        private long pending;
        private double passRate;
    }

    @Data
    @Builder
    public static class MaterialQCSummary {
        private String materialId;
        private String materialName;
        private String partNumber;
        private long totalTests;
        private long passed;
        private long failed;
        private long pending;
    }
}

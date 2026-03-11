package com.erplite.inventory.dto.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private long totalMaterials;
    private long totalActiveLots;
    private Map<String, Long> byStatus;
    private long nearExpiryLots;
    private long activeBatches;
    private long failedQCLast30Days;
    private LocalDateTime asOf;
}

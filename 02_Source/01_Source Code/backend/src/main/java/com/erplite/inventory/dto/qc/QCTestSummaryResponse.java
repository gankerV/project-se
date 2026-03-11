package com.erplite.inventory.dto.qc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QCTestSummaryResponse {
    private String lotId;
    private String lotStatus;
    private long totalTests;
    private long passed;
    private long failed;
    private long pending;
}

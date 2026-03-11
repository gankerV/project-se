package com.erplite.inventory.dto.batch;

import com.erplite.inventory.entity.ProductionBatch.BatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BatchStatusRequest {

    @NotNull(message = "Status is required")
    private BatchStatus status;

    private String performedBy;
}

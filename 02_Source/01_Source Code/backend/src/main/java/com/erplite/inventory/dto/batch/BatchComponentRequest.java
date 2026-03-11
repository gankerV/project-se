package com.erplite.inventory.dto.batch;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BatchComponentRequest {

    @NotBlank(message = "Lot ID is required")
    private String lotId;

    @NotNull(message = "Planned quantity is required")
    @DecimalMin(value = "0.001", message = "Planned quantity must be greater than 0")
    private BigDecimal plannedQuantity;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 20)
    private String unitOfMeasure;

    private String addedBy;
}

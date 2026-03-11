package com.erplite.inventory.dto.lot;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotSplitRequest {

    @NotNull(message = "Sample quantity is required")
    @DecimalMin(value = "0.001", message = "Sample quantity must be greater than 0")
    private BigDecimal sampleQuantity;

    @Size(max = 100)
    private String storageLocation;

    private String performedBy;
}

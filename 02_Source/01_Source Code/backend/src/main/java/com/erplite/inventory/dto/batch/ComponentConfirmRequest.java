package com.erplite.inventory.dto.batch;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComponentConfirmRequest {

    @NotNull(message = "Actual quantity is required")
    @DecimalMin(value = "0.001", message = "Actual quantity must be greater than 0")
    private BigDecimal actualQuantity;

    private String performedBy;
}

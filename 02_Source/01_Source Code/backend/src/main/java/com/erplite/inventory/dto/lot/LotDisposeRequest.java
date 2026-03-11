package com.erplite.inventory.dto.lot;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotDisposeRequest {

    @NotNull(message = "Disposal quantity is required")
    @DecimalMin(value = "0.001", message = "Disposal quantity must be greater than 0")
    private BigDecimal disposalQuantity;

    @NotBlank(message = "Reason is required")
    @Size(max = 500)
    private String reason;

    private String performedBy;
}

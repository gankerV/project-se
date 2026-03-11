package com.erplite.inventory.dto.lot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LotAdjustRequest {

    @NotNull(message = "Adjustment quantity is required")
    private BigDecimal adjustmentQuantity;

    @NotBlank(message = "Reason is required")
    @Size(max = 500)
    private String reason;

    private String performedBy;
}

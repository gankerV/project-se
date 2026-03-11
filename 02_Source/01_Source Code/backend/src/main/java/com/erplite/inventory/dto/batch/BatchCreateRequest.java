package com.erplite.inventory.dto.batch;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BatchCreateRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Batch number is required")
    @Size(max = 50)
    private String batchNumber;

    @NotNull(message = "Batch size is required")
    @DecimalMin(value = "0.001", message = "Batch size must be greater than 0")
    private BigDecimal batchSize;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 20)
    private String unitOfMeasure;

    @NotNull(message = "Manufacture date is required")
    private LocalDate manufactureDate;

    @NotNull(message = "Expiration date is required")
    private LocalDate expirationDate;
}

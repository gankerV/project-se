package com.erplite.inventory.dto.lot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LotTransferRequest {

    @NotBlank(message = "New storage location is required")
    @Size(max = 100)
    private String newStorageLocation;

    private String performedBy;

    @Size(max = 500)
    private String notes;
}

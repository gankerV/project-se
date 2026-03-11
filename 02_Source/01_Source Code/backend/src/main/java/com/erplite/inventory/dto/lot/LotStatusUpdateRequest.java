package com.erplite.inventory.dto.lot;

import com.erplite.inventory.entity.InventoryLot.LotStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LotStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private LotStatus status;

    private String performedBy;

    @Size(max = 500)
    private String notes;
}

package com.erplite.inventory.dto.report;

import com.erplite.inventory.entity.InventoryLot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class NearExpiryItemResponse {
    private String lotId;
    private String materialName;
    private String partNumber;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private String status;
    private String storageLocation;
    private LocalDate expirationDate;
    private int daysUntilExpiry;

    public static NearExpiryItemResponse from(InventoryLot lot) {
        NearExpiryItemResponse dto = new NearExpiryItemResponse();
        dto.setLotId(lot.getLotId());
        dto.setQuantity(lot.getQuantity());
        dto.setUnitOfMeasure(lot.getUnitOfMeasure());
        dto.setStatus(lot.getStatus() != null ? lot.getStatus().name() : null);
        dto.setStorageLocation(lot.getStorageLocation());
        dto.setExpirationDate(lot.getExpirationDate());
        if (lot.getExpirationDate() != null) {
            dto.setDaysUntilExpiry((int) ChronoUnit.DAYS.between(LocalDate.now(), lot.getExpirationDate()));
        }
        if (lot.getMaterial() != null) {
            dto.setMaterialName(lot.getMaterial().getMaterialName());
            dto.setPartNumber(lot.getMaterial().getPartNumber());
        }
        return dto;
    }
}

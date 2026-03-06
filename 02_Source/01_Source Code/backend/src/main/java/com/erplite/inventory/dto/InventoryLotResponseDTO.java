package com.erplite.inventory.dto;

import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryLotResponseDTO {

    private String lotId;
    private String materialId;
    private String materialName;
    private String partNumber;
    private String manufacturerLot;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private LotStatus status;
    private LocalDate receivedDate;
    private LocalDate expirationDate;
    private String storageLocation;
    private Boolean isSample;
    private String parentLotId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static InventoryLotResponseDTO fromEntity(InventoryLot lot) {
        InventoryLotResponseDTO dto = new InventoryLotResponseDTO();
        dto.setLotId(lot.getLotId());
        dto.setManufacturerLot(lot.getManufacturerLot());
        dto.setQuantity(lot.getQuantity());
        dto.setUnitOfMeasure(lot.getUnitOfMeasure());
        dto.setStatus(lot.getStatus());
        dto.setReceivedDate(lot.getReceivedDate());
        dto.setExpirationDate(lot.getExpirationDate());
        dto.setStorageLocation(lot.getStorageLocation());
        dto.setIsSample(lot.getIsSample());
        dto.setCreatedDate(lot.getCreatedDate());
        dto.setModifiedDate(lot.getModifiedDate());
        if (lot.getMaterial() != null) {
            dto.setMaterialId(lot.getMaterial().getMaterialId());
            dto.setMaterialName(lot.getMaterial().getMaterialName());
            dto.setPartNumber(lot.getMaterial().getPartNumber());
        }
        if (lot.getParentLot() != null) {
            dto.setParentLotId(lot.getParentLot().getLotId());
        }
        return dto;
    }
}

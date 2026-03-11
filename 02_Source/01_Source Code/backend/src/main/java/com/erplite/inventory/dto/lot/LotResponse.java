package com.erplite.inventory.dto.lot;

import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LotResponse {

    private String lotId;
    private String materialId;
    private String materialName;
    private String partNumber;
    private String manufacturerName;
    private String manufacturerLot;
    private String supplierName;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private LotStatus status;
    private LocalDate receivedDate;
    private LocalDate expirationDate;
    private LocalDate inUseExpirationDate;
    private String storageLocation;
    private Boolean isSample;
    private String parentLotId;
    private String poNumber;
    private String receivingFormId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static LotResponse from(InventoryLot lot) {
        LotResponse dto = new LotResponse();
        dto.setLotId(lot.getLotId());
        dto.setManufacturerName(lot.getManufacturerName());
        dto.setManufacturerLot(lot.getManufacturerLot());
        dto.setSupplierName(lot.getSupplierName());
        dto.setQuantity(lot.getQuantity());
        dto.setUnitOfMeasure(lot.getUnitOfMeasure());
        dto.setStatus(lot.getStatus());
        dto.setReceivedDate(lot.getReceivedDate());
        dto.setExpirationDate(lot.getExpirationDate());
        dto.setInUseExpirationDate(lot.getInUseExpirationDate());
        dto.setStorageLocation(lot.getStorageLocation());
        dto.setIsSample(lot.getIsSample());
        dto.setPoNumber(lot.getPoNumber());
        dto.setReceivingFormId(lot.getReceivingFormId());
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

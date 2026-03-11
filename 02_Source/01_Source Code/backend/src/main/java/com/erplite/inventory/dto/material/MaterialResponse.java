package com.erplite.inventory.dto.material;

import com.erplite.inventory.entity.Material;
import com.erplite.inventory.entity.Material.MaterialType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialResponse {

    private String materialId;
    private String partNumber;
    private String materialName;
    private MaterialType materialType;
    private String storageConditions;
    private String specificationDocument;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static MaterialResponse from(Material m) {
        MaterialResponse dto = new MaterialResponse();
        dto.setMaterialId(m.getMaterialId());
        dto.setPartNumber(m.getPartNumber());
        dto.setMaterialName(m.getMaterialName());
        dto.setMaterialType(m.getMaterialType());
        dto.setStorageConditions(m.getStorageConditions());
        dto.setSpecificationDocument(m.getSpecificationDocument());
        dto.setCreatedDate(m.getCreatedDate());
        dto.setModifiedDate(m.getModifiedDate());
        return dto;
    }
}

package com.erplite.inventory.dto;

import com.erplite.inventory.entity.Material;
import com.erplite.inventory.entity.Material.MaterialType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialResponseDTO {

    private String materialId;
    private String partNumber;
    private String materialName;
    private MaterialType materialType;
    private String storageConditions;
    private String specificationDocument;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static MaterialResponseDTO fromEntity(Material material) {
        MaterialResponseDTO dto = new MaterialResponseDTO();
        dto.setMaterialId(material.getMaterialId());
        dto.setPartNumber(material.getPartNumber());
        dto.setMaterialName(material.getMaterialName());
        dto.setMaterialType(material.getMaterialType());
        dto.setStorageConditions(material.getStorageConditions());
        dto.setSpecificationDocument(material.getSpecificationDocument());
        dto.setCreatedDate(material.getCreatedDate());
        dto.setModifiedDate(material.getModifiedDate());
        return dto;
    }
}

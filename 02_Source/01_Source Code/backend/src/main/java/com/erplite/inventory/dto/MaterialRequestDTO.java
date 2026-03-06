package com.erplite.inventory.dto;

import com.erplite.inventory.entity.Material.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialRequestDTO {

    @NotBlank(message = "Part number is required")
    @Size(max = 50, message = "Part number must not exceed 50 characters")
    private String partNumber;

    @NotBlank(message = "Material name is required")
    @Size(max = 200, message = "Material name must not exceed 200 characters")
    private String materialName;

    @NotNull(message = "Material type is required")
    private MaterialType materialType;

    @Size(max = 255)
    private String storageConditions;

    @Size(max = 500)
    private String specificationDocument;
}

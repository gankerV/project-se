package com.erplite.inventory.service;

import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.material.MaterialRequest;
import com.erplite.inventory.dto.material.MaterialResponse;
import com.erplite.inventory.entity.Material;
import com.erplite.inventory.entity.Material.MaterialType;
import com.erplite.inventory.exception.BusinessException;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.InventoryLotRepository;
import com.erplite.inventory.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final InventoryLotRepository inventoryLotRepository;

    public PagedResponse<MaterialResponse> listMaterials(String keyword, MaterialType type, Pageable pageable) {
        Page<Material> page;
        if (keyword != null && !keyword.isBlank() && type != null) {
            page = materialRepository.findByMaterialTypeAndMaterialNameContainingIgnoreCase(type, keyword, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            page = materialRepository.findByMaterialNameContainingIgnoreCase(keyword, pageable);
        } else if (type != null) {
            page = materialRepository.findByMaterialType(type, pageable);
        } else {
            page = materialRepository.findAll(pageable);
        }
        return PagedResponse.from(page.map(MaterialResponse::from));
    }

    public MaterialResponse getMaterialById(String id) {
        return MaterialResponse.from(findMaterialOrThrow(id));
    }

    @Transactional
    public MaterialResponse createMaterial(MaterialRequest req) {
        if (materialRepository.existsByPartNumber(req.getPartNumber())) {
            throw new BusinessException("Part number already exists: " + req.getPartNumber());
        }
        Material material = Material.builder()
            .partNumber(req.getPartNumber())
            .materialName(req.getMaterialName())
            .materialType(req.getMaterialType())
            .storageConditions(req.getStorageConditions())
            .specificationDocument(req.getSpecificationDocument())
            .build();
        return MaterialResponse.from(materialRepository.save(material));
    }

    @Transactional
    public MaterialResponse updateMaterial(String id, MaterialRequest req) {
        Material material = findMaterialOrThrow(id);
        if (materialRepository.existsByPartNumberAndMaterialIdNot(req.getPartNumber(), id)) {
            throw new BusinessException("Part number already used by another material: " + req.getPartNumber());
        }
        material.setPartNumber(req.getPartNumber());
        material.setMaterialName(req.getMaterialName());
        material.setMaterialType(req.getMaterialType());
        material.setStorageConditions(req.getStorageConditions());
        material.setSpecificationDocument(req.getSpecificationDocument());
        return MaterialResponse.from(materialRepository.save(material));
    }

    @Transactional
    public void deleteMaterial(String id) {
        findMaterialOrThrow(id);
        if (inventoryLotRepository.existsByMaterial_MaterialId(id)) {
            throw new BusinessException(
                "Cannot delete material with existing inventory lots. Deactivate the material instead.");
        }
        materialRepository.deleteById(id);
    }

    private Material findMaterialOrThrow(String id) {
        return materialRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
    }
}

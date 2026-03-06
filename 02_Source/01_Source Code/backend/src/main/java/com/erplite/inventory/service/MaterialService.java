package com.erplite.inventory.service;

import com.erplite.inventory.dto.MaterialRequestDTO;
import com.erplite.inventory.dto.MaterialResponseDTO;
import com.erplite.inventory.entity.Material;
import com.erplite.inventory.entity.Material.MaterialType;
import com.erplite.inventory.exception.BusinessException;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.InventoryLotRepository;
import com.erplite.inventory.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final InventoryLotRepository inventoryLotRepository;

    /**
     * Lấy tất cả vật tư, hỗ trợ filter theo keyword và/hoặc type.
     */
    public List<MaterialResponseDTO> getAllMaterials(String keyword, MaterialType type) {
        List<Material> materials;

        if (keyword != null && !keyword.isBlank() && type != null) {
            materials = materialRepository.findByMaterialTypeAndMaterialNameContainingIgnoreCase(type, keyword);
        } else if (keyword != null && !keyword.isBlank()) {
            materials = materialRepository.findByMaterialNameContainingIgnoreCase(keyword);
        } else if (type != null) {
            materials = materialRepository.findByMaterialType(type);
        } else {
            materials = materialRepository.findAll();
        }

        return materials.stream()
                .map(MaterialResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vật tư theo ID.
     */
    public MaterialResponseDTO getMaterialById(String id) {
        Material material = findMaterialOrThrow(id);
        return MaterialResponseDTO.fromEntity(material);
    }

    /**
     * Tạo vật tư mới. Kiểm tra part_number unique.
     */
    @Transactional
    public MaterialResponseDTO createMaterial(MaterialRequestDTO dto) {
        if (materialRepository.existsByPartNumber(dto.getPartNumber())) {
            throw new BusinessException("Part number already exists: " + dto.getPartNumber());
        }

        Material material = Material.builder()
                .partNumber(dto.getPartNumber())
                .materialName(dto.getMaterialName())
                .materialType(dto.getMaterialType())
                .storageConditions(dto.getStorageConditions())
                .specificationDocument(dto.getSpecificationDocument())
                .build();

        return MaterialResponseDTO.fromEntity(materialRepository.save(material));
    }

    /**
     * Cập nhật vật tư. Kiểm tra part_number unique (trừ chính nó).
     */
    @Transactional
    public MaterialResponseDTO updateMaterial(String id, MaterialRequestDTO dto) {
        Material material = findMaterialOrThrow(id);

        if (materialRepository.existsByPartNumberAndMaterialIdNot(dto.getPartNumber(), id)) {
            throw new BusinessException("Part number already used by another material: " + dto.getPartNumber());
        }

        material.setPartNumber(dto.getPartNumber());
        material.setMaterialName(dto.getMaterialName());
        material.setMaterialType(dto.getMaterialType());
        material.setStorageConditions(dto.getStorageConditions());
        material.setSpecificationDocument(dto.getSpecificationDocument());

        return MaterialResponseDTO.fromEntity(materialRepository.save(material));
    }

    /**
     * Xoá vật tư. Không cho phép nếu đã có lot liên kết.
     */
    @Transactional
    public void deleteMaterial(String id) {
        findMaterialOrThrow(id);

        if (inventoryLotRepository.existsByMaterial_MaterialId(id)) {
            throw new BusinessException(
                    "Cannot delete material with existing inventory lots. Deactivate the material instead.");
        }

        materialRepository.deleteById(id);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private Material findMaterialOrThrow(String id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
    }
}

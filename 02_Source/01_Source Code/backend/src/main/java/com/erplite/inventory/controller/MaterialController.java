package com.erplite.inventory.controller;

import com.erplite.inventory.dto.MaterialRequestDTO;
import com.erplite.inventory.dto.MaterialResponseDTO;
import com.erplite.inventory.entity.Material.MaterialType;
import com.erplite.inventory.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaterialController {

    private final MaterialService materialService;

    /**
     * GET /api/materials
     * Query params: keyword (optional), type (optional)
     */
    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> getAllMaterials(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MaterialType type) {
        return ResponseEntity.ok(materialService.getAllMaterials(keyword, type));
    }

    /**
     * GET /api/materials/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> getMaterialById(@PathVariable String id) {
        return ResponseEntity.ok(materialService.getMaterialById(id));
    }

    /**
     * POST /api/materials
     */
    @PostMapping
    public ResponseEntity<MaterialResponseDTO> createMaterial(@Valid @RequestBody MaterialRequestDTO dto) {
        MaterialResponseDTO created = materialService.createMaterial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/materials/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> updateMaterial(
            @PathVariable String id,
            @Valid @RequestBody MaterialRequestDTO dto) {
        return ResponseEntity.ok(materialService.updateMaterial(id, dto));
    }

    /**
     * DELETE /api/materials/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}

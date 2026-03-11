package com.erplite.inventory.controller;

import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.material.MaterialRequest;
import com.erplite.inventory.dto.material.MaterialResponse;
import com.erplite.inventory.entity.Material.MaterialType;
import com.erplite.inventory.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<MaterialResponse>> listMaterials(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MaterialType type,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable) {
        return ResponseEntity.ok(materialService.listMaterials(keyword, type, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialResponse> getMaterial(@PathVariable String id) {
        return ResponseEntity.ok(materialService.getMaterialById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','InventoryManager')")
    public ResponseEntity<MaterialResponse> createMaterial(@Valid @RequestBody MaterialRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.createMaterial(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','InventoryManager')")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable String id, @Valid @RequestBody MaterialRequest req) {
        return ResponseEntity.ok(materialService.updateMaterial(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}

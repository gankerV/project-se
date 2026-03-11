package com.erplite.inventory.controller;

import com.erplite.inventory.dto.label.*;
import com.erplite.inventory.entity.LabelTemplate.LabelType;
import com.erplite.inventory.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/labels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/templates")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LabelTemplateResponse>> listTemplates(
            @RequestParam(required = false) LabelType labelType) {
        return ResponseEntity.ok(labelService.listTemplates(labelType));
    }

    @GetMapping("/templates/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelTemplateResponse> getTemplate(@PathVariable String id) {
        return ResponseEntity.ok(labelService.getTemplateById(id));
    }

    @PostMapping("/templates")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<LabelTemplateResponse> createTemplate(
            @Valid @RequestBody LabelTemplateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(labelService.createTemplate(req));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<LabelTemplateResponse> updateTemplate(
            @PathVariable String id, @Valid @RequestBody LabelTemplateRequest req) {
        return ResponseEntity.ok(labelService.updateTemplate(id, req));
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        labelService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('Admin','InventoryManager','QualityControl','Production')")
    public ResponseEntity<LabelGenerateResponse> generateLabel(
            @Valid @RequestBody LabelGenerateRequest req) {
        return ResponseEntity.ok(labelService.generateLabel(req));
    }
}

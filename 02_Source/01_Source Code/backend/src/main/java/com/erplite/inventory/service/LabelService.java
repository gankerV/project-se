package com.erplite.inventory.service;

import com.erplite.inventory.dto.label.*;
import com.erplite.inventory.entity.InventoryLot;
import com.erplite.inventory.entity.LabelTemplate;
import com.erplite.inventory.entity.LabelTemplate.LabelType;
import com.erplite.inventory.entity.ProductionBatch;
import com.erplite.inventory.exception.BusinessException;
import com.erplite.inventory.exception.ConflictException;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.InventoryLotRepository;
import com.erplite.inventory.repository.LabelTemplateRepository;
import com.erplite.inventory.repository.ProductionBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelService {

    private final LabelTemplateRepository templateRepository;
    private final InventoryLotRepository lotRepository;
    private final ProductionBatchRepository batchRepository;

    public List<LabelTemplateResponse> listTemplates(LabelType labelType) {
        List<LabelTemplate> templates = labelType != null
            ? templateRepository.findByLabelType(labelType)
            : templateRepository.findAll();
        return templates.stream().map(LabelTemplateResponse::from).collect(Collectors.toList());
    }

    public LabelTemplateResponse getTemplateById(String id) {
        return LabelTemplateResponse.from(findTemplateOrThrow(id));
    }

    @Transactional
    public LabelTemplateResponse createTemplate(LabelTemplateRequest req) {
        if (templateRepository.existsByTemplateId(req.getTemplateId())) {
            throw new ConflictException("Template ID already exists: " + req.getTemplateId());
        }
        LabelTemplate template = LabelTemplate.builder()
            .templateId(req.getTemplateId())
            .templateName(req.getTemplateName())
            .labelType(req.getLabelType())
            .templateContent(req.getTemplateContent())
            .width(req.getWidth())
            .height(req.getHeight())
            .build();
        return LabelTemplateResponse.from(templateRepository.save(template));
    }

    @Transactional
    public LabelTemplateResponse updateTemplate(String id, LabelTemplateRequest req) {
        LabelTemplate template = findTemplateOrThrow(id);
        template.setTemplateName(req.getTemplateName());
        template.setLabelType(req.getLabelType());
        template.setTemplateContent(req.getTemplateContent());
        template.setWidth(req.getWidth());
        template.setHeight(req.getHeight());
        return LabelTemplateResponse.from(templateRepository.save(template));
    }

    @Transactional
    public void deleteTemplate(String id) {
        findTemplateOrThrow(id);
        templateRepository.deleteById(id);
    }

    public LabelGenerateResponse generateLabel(LabelGenerateRequest req) {
        LabelTemplate template = findTemplateOrThrow(req.getTemplateId());
        String content = template.getTemplateContent();

        if (req.getSourceType() == LabelGenerateRequest.SourceType.LOT) {
            InventoryLot lot = lotRepository.findById(req.getSourceId())
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLot", "id", req.getSourceId()));
            content = content
                .replace("{{lotId}}", nullToEmpty(lot.getLotId()))
                .replace("{{manufacturerLot}}", nullToEmpty(lot.getManufacturerLot()))
                .replace("{{materialName}}", lot.getMaterial() != null ? nullToEmpty(lot.getMaterial().getMaterialName()) : "")
                .replace("{{partNumber}}", lot.getMaterial() != null ? nullToEmpty(lot.getMaterial().getPartNumber()) : "")
                .replace("{{quantity}}", lot.getQuantity() != null ? lot.getQuantity().toPlainString() : "")
                .replace("{{unitOfMeasure}}", nullToEmpty(lot.getUnitOfMeasure()))
                .replace("{{expirationDate}}", lot.getExpirationDate() != null ? lot.getExpirationDate().toString() : "")
                .replace("{{storageLocation}}", nullToEmpty(lot.getStorageLocation()))
                .replace("{{status}}", lot.getStatus() != null ? lot.getStatus().name() : "");
        } else if (req.getSourceType() == LabelGenerateRequest.SourceType.BATCH) {
            ProductionBatch batch = batchRepository.findById(req.getSourceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductionBatch", "id", req.getSourceId()));
            content = content
                .replace("{{batchId}}", nullToEmpty(batch.getBatchId()))
                .replace("{{batchNumber}}", nullToEmpty(batch.getBatchNumber()))
                .replace("{{productName}}", batch.getProduct() != null ? nullToEmpty(batch.getProduct().getMaterialName()) : "")
                .replace("{{batchSize}}", batch.getBatchSize() != null ? batch.getBatchSize().toPlainString() : "")
                .replace("{{unitOfMeasure}}", nullToEmpty(batch.getUnitOfMeasure()))
                .replace("{{expirationDate}}", batch.getExpirationDate() != null ? batch.getExpirationDate().toString() : "")
                .replace("{{status}}", batch.getStatus() != null ? batch.getStatus().name() : "");
        } else {
            throw new BusinessException("Unsupported source type: " + req.getSourceType());
        }

        return LabelGenerateResponse.builder()
            .templateId(template.getTemplateId())
            .templateName(template.getTemplateName())
            .labelType(template.getLabelType() != null ? template.getLabelType().name() : null)
            .renderedContent(content)
            .width(template.getWidth())
            .height(template.getHeight())
            .generatedAt(LocalDateTime.now())
            .build();
    }

    private LabelTemplate findTemplateOrThrow(String id) {
        return templateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LabelTemplate", "id", id));
    }

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }
}

package com.erplite.inventory.dto.batch;

import com.erplite.inventory.entity.ProductionBatch;
import com.erplite.inventory.entity.ProductionBatch.BatchStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BatchResponse {

    private String batchId;
    private String productId;
    private String productName;
    private String batchNumber;
    private BigDecimal batchSize;
    private String unitOfMeasure;
    private LocalDate manufactureDate;
    private LocalDate expirationDate;
    private BatchStatus status;
    private List<ComponentResponse> components;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static BatchResponse from(ProductionBatch batch, boolean includeComponents) {
        BatchResponse dto = new BatchResponse();
        dto.setBatchId(batch.getBatchId());
        dto.setBatchNumber(batch.getBatchNumber());
        dto.setBatchSize(batch.getBatchSize());
        dto.setUnitOfMeasure(batch.getUnitOfMeasure());
        dto.setManufactureDate(batch.getManufactureDate());
        dto.setExpirationDate(batch.getExpirationDate());
        dto.setStatus(batch.getStatus());
        dto.setCreatedDate(batch.getCreatedDate());
        dto.setModifiedDate(batch.getModifiedDate());
        if (batch.getProduct() != null) {
            dto.setProductId(batch.getProduct().getMaterialId());
            dto.setProductName(batch.getProduct().getMaterialName());
        }
        if (includeComponents && batch.getComponents() != null) {
            dto.setComponents(batch.getComponents().stream()
                .map(ComponentResponse::from).collect(Collectors.toList()));
        }
        return dto;
    }
}

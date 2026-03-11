package com.erplite.inventory.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LabelGenerateRequest {

    @NotBlank(message = "Template ID is required")
    private String templateId;

    @NotNull(message = "Source type is required")
    private SourceType sourceType;

    @NotBlank(message = "Source ID is required")
    private String sourceId;

    public enum SourceType {
        LOT, BATCH
    }
}

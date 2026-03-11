package com.erplite.inventory.dto.label;

import com.erplite.inventory.entity.LabelTemplate.LabelType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LabelTemplateRequest {

    @NotBlank(message = "Template ID is required")
    @Size(max = 20)
    private String templateId;

    @NotBlank(message = "Template name is required")
    @Size(max = 200)
    private String templateName;

    @NotNull(message = "Label type is required")
    private LabelType labelType;

    @NotBlank(message = "Template content is required")
    private String templateContent;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.01", message = "Width must be greater than 0")
    private BigDecimal width;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.01", message = "Height must be greater than 0")
    private BigDecimal height;
}

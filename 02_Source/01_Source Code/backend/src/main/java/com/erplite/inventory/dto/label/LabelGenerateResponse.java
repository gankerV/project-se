package com.erplite.inventory.dto.label;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LabelGenerateResponse {
    private String templateId;
    private String templateName;
    private String labelType;
    private String renderedContent;
    private BigDecimal width;
    private BigDecimal height;
    private LocalDateTime generatedAt;
}

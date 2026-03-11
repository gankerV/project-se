package com.erplite.inventory.dto.label;

import com.erplite.inventory.entity.LabelTemplate;
import com.erplite.inventory.entity.LabelTemplate.LabelType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LabelTemplateResponse {

    private String templateId;
    private String templateName;
    private LabelType labelType;
    private String templateContent;
    private BigDecimal width;
    private BigDecimal height;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static LabelTemplateResponse from(LabelTemplate t) {
        LabelTemplateResponse dto = new LabelTemplateResponse();
        dto.setTemplateId(t.getTemplateId());
        dto.setTemplateName(t.getTemplateName());
        dto.setLabelType(t.getLabelType());
        dto.setTemplateContent(t.getTemplateContent());
        dto.setWidth(t.getWidth());
        dto.setHeight(t.getHeight());
        dto.setCreatedDate(t.getCreatedDate());
        dto.setModifiedDate(t.getModifiedDate());
        return dto;
    }
}

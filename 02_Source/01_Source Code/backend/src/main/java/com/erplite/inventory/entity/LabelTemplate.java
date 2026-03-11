package com.erplite.inventory.entity;

import com.erplite.inventory.converter.LabelTypeConverter;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LabelTemplates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelTemplate {

    @Id
    @Column(name = "template_id", length = 20)
    private String templateId;

    @Column(name = "template_name", nullable = false, length = 200)
    private String templateName;

    @Convert(converter = LabelTypeConverter.class)
    @Column(name = "label_type", nullable = false, length = 20)
    private LabelType labelType;

    @Column(name = "template_content", columnDefinition = "TEXT")
    private String templateContent;

    @Column(name = "width", precision = 5, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    public enum LabelType {
        RAW_MATERIAL, FINISHED_PRODUCT, SAMPLE, QUARANTINE
    }
}

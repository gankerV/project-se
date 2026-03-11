package com.erplite.inventory.entity;

import com.erplite.inventory.converter.MaterialTypeConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @Column(name = "material_id", length = 36)
    private String materialId;

    @Column(name = "part_number", nullable = false, unique = true, length = 50)
    private String partNumber;

    @Column(name = "material_name", nullable = false, length = 200)
    private String materialName;

    @Convert(converter = MaterialTypeConverter.class)
    @Column(name = "material_type", nullable = false, length = 25)
    private MaterialType materialType;

    @Column(name = "storage_conditions", length = 255)
    private String storageConditions;

    @Column(name = "specification_document", length = 500)
    private String specificationDocument;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        if (materialId == null || materialId.isBlank()) {
            materialId = UUID.randomUUID().toString();
        }
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    public enum MaterialType {
        API, EXCIPIENT, DIETARY_SUPPLEMENT, CONTAINER, CLOSURE, PROCESS_CHEMICAL, TESTING_MATERIAL
    }
}

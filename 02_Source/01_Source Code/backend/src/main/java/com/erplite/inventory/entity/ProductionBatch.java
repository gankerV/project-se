package com.erplite.inventory.entity;

import com.erplite.inventory.converter.BatchStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ProductionBatches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionBatch {

    @Id
    @Column(name = "batch_id", length = 36)
    private String batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Material product;

    @Column(name = "batch_number", nullable = false, unique = true, length = 50)
    private String batchNumber;

    @Column(name = "batch_size", precision = 15, scale = 3)
    private BigDecimal batchSize;

    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Builder.Default
    @Convert(converter = BatchStatusConverter.class)
    @Column(name = "status", nullable = false, length = 15)
    private BatchStatus status = BatchStatus.IN_PROGRESS;

    @Builder.Default
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BatchComponent> components = new ArrayList<>();

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        if (batchId == null || batchId.isBlank()) {
            batchId = UUID.randomUUID().toString();
        }
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
        if (status == null) status = BatchStatus.IN_PROGRESS;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    public enum BatchStatus {
        IN_PROGRESS, COMPLETED, CANCELLED, ON_HOLD
    }
}

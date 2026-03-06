package com.erplite.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @Column(name = "transaction_id", length = 36)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private InventoryLot lot;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    // Positive: stock in (Receipt), Negative: stock out (Usage/Disposal)
    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "performed_by", length = 50)
    private String performedBy;

    @PrePersist
    protected void onCreate() {
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    public enum TransactionType {
        Receipt, Usage, Split, Transfer, Adjustment, Disposal
    }
}

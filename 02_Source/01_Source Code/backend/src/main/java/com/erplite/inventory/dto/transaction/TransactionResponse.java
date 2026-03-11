package com.erplite.inventory.dto.transaction;

import com.erplite.inventory.entity.InventoryTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private String transactionId;
    private String lotId;
    private String transactionType;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private LocalDateTime transactionDate;
    private String referenceId;
    private String notes;
    private String performedBy;

    public static TransactionResponse from(InventoryTransaction tx) {
        TransactionResponse dto = new TransactionResponse();
        dto.setTransactionId(tx.getTransactionId());
        dto.setTransactionType(tx.getTransactionType() != null ? tx.getTransactionType().name() : null);
        dto.setQuantity(tx.getQuantity());
        dto.setUnitOfMeasure(tx.getUnitOfMeasure());
        dto.setTransactionDate(tx.getTransactionDate());
        dto.setReferenceId(tx.getReferenceId());
        dto.setNotes(tx.getNotes());
        dto.setPerformedBy(tx.getPerformedBy());
        if (tx.getLot() != null) {
            dto.setLotId(tx.getLot().getLotId());
        }
        return dto;
    }
}

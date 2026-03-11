package com.erplite.inventory.converter;

import com.erplite.inventory.entity.ProductionBatch.BatchStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = true)
public class BatchStatusConverter implements AttributeConverter<BatchStatus, String> {

    private static final Map<BatchStatus, String> TO_DB = Map.of(
        BatchStatus.IN_PROGRESS, "In Progress",
        BatchStatus.COMPLETED, "Completed",
        BatchStatus.CANCELLED, "Cancelled",
        BatchStatus.ON_HOLD, "On Hold"
    );

    private static final Map<String, BatchStatus> FROM_DB = Map.of(
        "In Progress", BatchStatus.IN_PROGRESS,
        "Completed", BatchStatus.COMPLETED,
        "Cancelled", BatchStatus.CANCELLED,
        "On Hold", BatchStatus.ON_HOLD
    );

    @Override
    public String convertToDatabaseColumn(BatchStatus attribute) {
        if (attribute == null) return null;
        String value = TO_DB.get(attribute);
        if (value == null) throw new IllegalArgumentException("Unknown BatchStatus: " + attribute);
        return value;
    }

    @Override
    public BatchStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        BatchStatus value = FROM_DB.get(dbData);
        if (value == null) throw new IllegalArgumentException("Unknown DB value for BatchStatus: " + dbData);
        return value;
    }
}

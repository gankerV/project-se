package com.erplite.inventory.converter;

import com.erplite.inventory.entity.LabelTemplate.LabelType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = true)
public class LabelTypeConverter implements AttributeConverter<LabelType, String> {

    private static final Map<LabelType, String> TO_DB = Map.of(
        LabelType.RAW_MATERIAL, "Raw Material",
        LabelType.FINISHED_PRODUCT, "Finished Product",
        LabelType.SAMPLE, "Sample",
        LabelType.QUARANTINE, "Quarantine"
    );

    private static final Map<String, LabelType> FROM_DB = Map.of(
        "Raw Material", LabelType.RAW_MATERIAL,
        "Finished Product", LabelType.FINISHED_PRODUCT,
        "Sample", LabelType.SAMPLE,
        "Quarantine", LabelType.QUARANTINE
    );

    @Override
    public String convertToDatabaseColumn(LabelType attribute) {
        if (attribute == null) return null;
        String value = TO_DB.get(attribute);
        if (value == null) throw new IllegalArgumentException("Unknown LabelType: " + attribute);
        return value;
    }

    @Override
    public LabelType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        LabelType value = FROM_DB.get(dbData);
        if (value == null) throw new IllegalArgumentException("Unknown DB value for LabelType: " + dbData);
        return value;
    }
}

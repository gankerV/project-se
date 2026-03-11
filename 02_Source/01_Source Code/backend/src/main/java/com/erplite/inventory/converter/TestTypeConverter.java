package com.erplite.inventory.converter;

import com.erplite.inventory.entity.QCTest.TestType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = true)
public class TestTypeConverter implements AttributeConverter<TestType, String> {

    private static final Map<TestType, String> TO_DB = Map.of(
        TestType.IDENTITY, "Identity",
        TestType.PURITY, "Purity",
        TestType.POTENCY, "Potency",
        TestType.MICROBIAL, "Microbial",
        TestType.GROWTH_PROMOTION, "Growth Promotion",
        TestType.APPEARANCE, "Appearance",
        TestType.OTHER, "Other"
    );

    private static final Map<String, TestType> FROM_DB = Map.of(
        "Identity", TestType.IDENTITY,
        "Purity", TestType.PURITY,
        "Potency", TestType.POTENCY,
        "Microbial", TestType.MICROBIAL,
        "Growth Promotion", TestType.GROWTH_PROMOTION,
        "Appearance", TestType.APPEARANCE,
        "Other", TestType.OTHER
    );

    @Override
    public String convertToDatabaseColumn(TestType attribute) {
        if (attribute == null) return null;
        String value = TO_DB.get(attribute);
        if (value == null) throw new IllegalArgumentException("Unknown TestType: " + attribute);
        return value;
    }

    @Override
    public TestType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        TestType value = FROM_DB.get(dbData);
        if (value == null) throw new IllegalArgumentException("Unknown DB value for TestType: " + dbData);
        return value;
    }
}

package com.erplite.inventory.converter;

import com.erplite.inventory.entity.Material.MaterialType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = true)
public class MaterialTypeConverter implements AttributeConverter<MaterialType, String> {

    private static final Map<MaterialType, String> TO_DB = Map.of(
        MaterialType.API, "API",
        MaterialType.EXCIPIENT, "Excipient",
        MaterialType.DIETARY_SUPPLEMENT, "Dietary Supplement",
        MaterialType.CONTAINER, "Container",
        MaterialType.CLOSURE, "Closure",
        MaterialType.PROCESS_CHEMICAL, "Process Chemical",
        MaterialType.TESTING_MATERIAL, "Testing Material"
    );

    private static final Map<String, MaterialType> FROM_DB = Map.of(
        "API", MaterialType.API,
        "Excipient", MaterialType.EXCIPIENT,
        "Dietary Supplement", MaterialType.DIETARY_SUPPLEMENT,
        "Container", MaterialType.CONTAINER,
        "Closure", MaterialType.CLOSURE,
        "Process Chemical", MaterialType.PROCESS_CHEMICAL,
        "Testing Material", MaterialType.TESTING_MATERIAL
    );

    @Override
    public String convertToDatabaseColumn(MaterialType attribute) {
        if (attribute == null) return null;
        String value = TO_DB.get(attribute);
        if (value == null) throw new IllegalArgumentException("Unknown MaterialType: " + attribute);
        return value;
    }

    @Override
    public MaterialType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        MaterialType value = FROM_DB.get(dbData);
        if (value == null) throw new IllegalArgumentException("Unknown DB value for MaterialType: " + dbData);
        return value;
    }
}

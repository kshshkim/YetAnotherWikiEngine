package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = true)
public class PermissionLevelConverter implements AttributeConverter<PermissionLevel, Integer> {
    static final int MAX_PERMISSION_VALUE = 127;
    private final PermissionLevel[] permissionLevels = new PermissionLevel[MAX_PERMISSION_VALUE + 1];

    public PermissionLevelConverter() {
        Arrays.stream(PermissionLevel.values())
                .forEach(pl -> permissionLevels[pl.getIntValue()] = pl);
    }

    @Override
    public Integer convertToDatabaseColumn(PermissionLevel attribute) {
        return attribute.getIntValue();
    }

    @Override
    public PermissionLevel convertToEntityAttribute(Integer dbData) {
        if (dbData > MAX_PERMISSION_VALUE || permissionLevels[dbData] == null) {
            throw new IllegalArgumentException("cannot convert dbData " + dbData + " to PermissionLevel. integer value does not have any matching PermissionLevel value");
        }
        return permissionLevels[dbData];
    }
}

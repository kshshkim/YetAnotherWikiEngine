package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.PermissionLevel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionLevelConverterTest {

    PermissionLevelConverter permissionLevelConverter = new PermissionLevelConverter();

    @Test
    void convertToDatabaseColumn() {
        List<Integer> converted = Arrays.stream(PermissionLevel.values())
                .map(pl -> permissionLevelConverter.convertToDatabaseColumn(pl))
                .toList();

        List<Integer> expected = Arrays.stream(PermissionLevel.values())
                .map(PermissionLevel::getIntValue)
                .toList();

        assertThat(converted)
                .containsExactlyElementsOf(expected);
    }

    @Test
    void convertToEntityAttribute() {
        List<PermissionLevel> converted = Arrays.stream(PermissionLevel.values())
                .map(pl -> permissionLevelConverter.convertToEntityAttribute(pl.getIntValue()))
                .toList();

        assertThat(converted)
                .containsExactlyElementsOf(Arrays.asList(PermissionLevel.values()));
    }

    @Test
    void convertToEntityAttribute_invalid_values() {
        Integer tooBig = PermissionLevelConverter.MAX_PERMISSION_VALUE + 1;
        Integer notImplemented = PermissionLevelConverter.MAX_PERMISSION_VALUE;

        assertThatThrownBy(() -> permissionLevelConverter.convertToEntityAttribute(tooBig))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(tooBig));

        assertThatThrownBy(() -> permissionLevelConverter.convertToEntityAttribute(notImplemented))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(notImplemented));
    }
}
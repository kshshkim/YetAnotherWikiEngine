package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.common.model.Namespace;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JpaNamespaceConverterTest {
    JpaNamespaceConverter jpaNamespaceConverter = new JpaNamespaceConverter();

    @Test
    void convertToDatabaseColumn() {
        Namespace[] values = Namespace.values();
        for (Namespace namespace : values) {
            Integer converted = jpaNamespaceConverter.convertToDatabaseColumn(namespace);
            assertThat(converted)
                    .describedAs("정수형 데이터로 적절히 변환돼야함.")
                    .isEqualTo(namespace.getIntValue());
        }
    }

    @Test
    void convertToEntityAttribute() {
        for (Namespace namespace : Namespace.values()) {
            Namespace converted = jpaNamespaceConverter.convertToEntityAttribute(namespace.getIntValue());
            assertThat(converted)
                    .describedAs("Enum 인스턴스로 적절히 변환돼야함.")
                    .isEqualTo(namespace);
        }
    }
}
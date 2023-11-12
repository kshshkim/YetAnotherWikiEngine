package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NamespaceConverterTest {
    NamespaceConverter namespaceConverter = new NamespaceConverter();

    @Test
    void convertToDatabaseColumn() {
        Namespace[] values = Namespace.values();
        for (Namespace namespace : values) {
            Integer converted = namespaceConverter.convertToDatabaseColumn(namespace);
            assertThat(converted)
                    .describedAs("정수형 데이터로 적절히 변환돼야함.")
                    .isEqualTo(namespace.getIntValue());
        }
    }

    @Test
    void convertToEntityAttribute() {
        for (Namespace namespace : Namespace.values()) {
            Namespace converted = namespaceConverter.convertToEntityAttribute(namespace.getIntValue());
            assertThat(converted)
                    .describedAs("Enum 인스턴스로 적절히 변환돼야함.")
                    .isEqualTo(namespace);
        }
    }
}
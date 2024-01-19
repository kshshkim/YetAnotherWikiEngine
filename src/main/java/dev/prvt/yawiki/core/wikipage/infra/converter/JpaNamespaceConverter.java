package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

/**
 * JPA Converter.
 * @see Namespace
 */
@Converter(autoApply = true)
public class JpaNamespaceConverter implements AttributeConverter<Namespace, Integer> {

    /**
     * 변환시마다 인스턴스가 생성되는 것을 막고, 오버헤드 없이 빠르게 변환되도록 내부에 배열을 가짐. 생성 시점에 초기화됨.
     * 배열의 인덱스는 namespace.intValue와 일치함.
     */

    public JpaNamespaceConverter() {
    }

    @Override
    public Integer convertToDatabaseColumn(Namespace attribute) {
        return attribute.getIntValue();
    }

    @Override
    public Namespace convertToEntityAttribute(Integer dbData) {
        return Namespace.valueOf(dbData);
    }
}

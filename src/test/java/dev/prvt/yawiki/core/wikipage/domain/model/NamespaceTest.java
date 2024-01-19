package dev.prvt.yawiki.core.wikipage.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class NamespaceTest {

    @ParameterizedTest
    @EnumSource(Namespace.class)
    void valueOf(Namespace namespace) {
        assertThat(Namespace.valueOf(namespace.getIntValue()))
            .describedAs("int 값에 해당하는 Namespace 반환해야함. 새로운 인스턴스를 생성하지 않음.")
            .isSameAs(namespace);
    }

}
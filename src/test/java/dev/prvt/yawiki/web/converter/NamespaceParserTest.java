package dev.prvt.yawiki.web.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class NamespaceParserTest {
    Map<String, Namespace> identifierMap;
    NamespaceParser namespaceParser;
    @BeforeEach
    void init() {
        identifierMap = new HashMap<>();
        identifierMap.put("파일", Namespace.FILE);
        namespaceParser = new NamespaceParser(identifierMap);
    }

    @Test
    void parseTitle_with_whitespace_a() {
        // given
        String givenTitle = "파일: asdf.png";

        // when
        Namespace result = namespaceParser.getNamespace(givenTitle);

        // then
        assertThat(result)
                .isEqualTo(Namespace.FILE);
    }


    @Test
    void parseTitle_with_whitespace_b() {
        // given
        String givenTitle = "파일 :asdf.png";

        // when
        Namespace result = namespaceParser.getNamespace(givenTitle);

        // then
        assertThat(result)
                .isEqualTo(Namespace.FILE);
    }

    @Test
    void parseTitle_with_whitespace_c() {
        // given
        String givenTitle = "파일 : asdf.png";

        // when
        Namespace result = namespaceParser.getNamespace(givenTitle);

        // then
        assertThat(result)
                .isEqualTo(Namespace.FILE);
    }

    @Test
    void parseTitle_without_whitespace() {
        // given
        String givenTitle = "파일:asdf.png";

        // when
        Namespace result = namespaceParser.getNamespace(givenTitle);

        // then
        assertThat(result)
                .isEqualTo(Namespace.FILE);
    }

    @ParameterizedTest
    @EnumSource(Namespace.class)
    void noArgsConstructorTest(Namespace namespace) {
        NamespaceParser defaultParser = new NamespaceParser();

        // when
        Namespace parsed = defaultParser.getNamespace(namespace.name() + ":" + randString());

        // then
        assertThat(parsed).isEqualTo(namespace);
    }
}
package dev.prvt.yawiki.core.wikipage.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawContentTest {
    @Test
    void size_should_be_calculated() {
        String size17 = "size should be 17";
        RawContent rawContent = new RawContent(size17);

        assertThat(rawContent.getSize())
                .isEqualTo(size17.length());
    }

    @Test
    void size_should_be_calculated_0_with_blank_content() {
        String size0 = "";

        RawContent rawContent = new RawContent(size0);

        assertThat(rawContent.getSize())
                .isEqualTo(0);
    }

    @Test
    void content_must_not_be_null() {
        assertThatThrownBy(() -> new RawContent(null))
                .isInstanceOf(NullPointerException.class);
    }
}
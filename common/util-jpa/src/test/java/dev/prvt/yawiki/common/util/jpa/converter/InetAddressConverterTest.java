package dev.prvt.yawiki.common.util.jpa.converter;

import static dev.prvt.yawiki.common.util.test.Fixture.aInetV4Address;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import org.junit.jupiter.api.Test;

class InetAddressConverterTest {
    InetAddressConverter inetAddressConverter = new InetAddressConverter();
    @Test
    void convertToDatabaseColumn() {
        // given
        InetAddress given = aInetV4Address();

        // when
        InetAddress converted = inetAddressConverter.convertToEntityAttribute(given.getHostAddress());

        // then
        assertThat(converted)
                .isEqualTo(given);
    }

    @Test
    void convertToEntityAttribute() {
        // given
        InetAddress given = aInetV4Address();

        // when
        String converted = inetAddressConverter.convertToDatabaseColumn(given);

        // then
        assertThat(converted)
                .isEqualTo(given.getHostAddress());
    }
}
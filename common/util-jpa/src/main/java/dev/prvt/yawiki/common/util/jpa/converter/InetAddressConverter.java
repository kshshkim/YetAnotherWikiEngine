package dev.prvt.yawiki.common.util.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Converter
public class InetAddressConverter implements AttributeConverter<InetAddress, String> {
    @Override
    public String convertToDatabaseColumn(InetAddress attribute) {
        return attribute == null ? null : attribute.getHostAddress();
    }

    @Override
    public InetAddress convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            try {
                return InetAddress.getByName(dbData);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

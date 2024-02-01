package dev.prvt.yawiki.common.util;

import java.time.Instant;
import java.time.LocalDateTime;

public class CurrentTimeProvider {

    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    public Instant getCurrentInstant() {
        return Instant.now();
    }
}

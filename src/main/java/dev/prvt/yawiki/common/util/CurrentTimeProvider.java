package dev.prvt.yawiki.common.util;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CurrentTimeProvider {

    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
}

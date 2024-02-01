package dev.prvt.yawiki.common.util.test;

import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FixedCurrentTimeProvider extends CurrentTimeProvider {

    private final Instant fixedInstant;
    private final LocalDateTime fixedLocalDateTime;


    public FixedCurrentTimeProvider(Instant fixed) {
        this.fixedInstant = fixed;
        this.fixedLocalDateTime = LocalDateTime.ofInstant(fixed, ZoneId.systemDefault());
    }

    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return fixedLocalDateTime;
    }

    @Override
    public Instant getCurrentInstant() {
        return fixedInstant;
    }
}

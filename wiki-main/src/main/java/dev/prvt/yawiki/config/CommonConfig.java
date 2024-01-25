package dev.prvt.yawiki.config;

import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public CurrentTimeProvider currentTimeProvider() {
        return new CurrentTimeProvider();
    }
}

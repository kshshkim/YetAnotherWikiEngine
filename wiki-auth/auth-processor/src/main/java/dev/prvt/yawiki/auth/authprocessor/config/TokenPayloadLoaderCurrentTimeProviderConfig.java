package dev.prvt.yawiki.auth.authprocessor.config;

import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenPayloadLoaderCurrentTimeProviderConfig {

    @Bean("tokenPayloadLoaderCurrentTimeProvider")
    public CurrentTimeProvider tokenPayloadLoaderCurrentTimeProvider() {
        return new CurrentTimeProvider();
    }

}

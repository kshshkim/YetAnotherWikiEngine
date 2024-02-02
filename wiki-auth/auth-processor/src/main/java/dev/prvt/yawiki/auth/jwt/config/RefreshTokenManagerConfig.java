package dev.prvt.yawiki.auth.jwt.config;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.infra.jpaimpl.JpaRefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.infra.jpaimpl.RefreshTokenRepository;
import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenManagerConfig {

    @Bean("refreshTokenManagerCurrentTimeProvider")
    public CurrentTimeProvider refreshTokenManagerCurrentTimeProvider() {
        return new CurrentTimeProvider();
    }

    @Bean
    public RefreshTokenManager refreshTokenManager(
        RefreshTokenRepository refreshTokenRepository,
        @Value("${yawiki.jwt.auth.refresh-token.lifespan}") int refreshTokenLifespan
    ) {
        return new JpaRefreshTokenManager(
            refreshTokenRepository,
            refreshTokenManagerCurrentTimeProvider(),
            refreshTokenLifespan
        );
    }

}

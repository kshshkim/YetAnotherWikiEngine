package dev.prvt.yawiki.auth.jwt.config;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.infra.AccessTokenGeneratorImpl;
import dev.prvt.yawiki.auth.jwt.infra.jpaimpl.JpaRefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.infra.jpaimpl.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;

@Configuration
public class JwtIssuerConfig {

    @Bean
    public AccessTokenGenerator accessTokenGenerator(
        JwtEncoder jwtEncoder
    ) {
        return new AccessTokenGeneratorImpl(jwtEncoder);
    }

    @Bean
    public RefreshTokenManager refreshTokenManager(
        RefreshTokenRepository refreshTokenRepository,
        @Value("${yawiki.jwt.auth.refresh-token.lifespan}") int refreshTokenLifespan
    ) {
        return new JpaRefreshTokenManager(refreshTokenRepository, refreshTokenLifespan);
    }

}

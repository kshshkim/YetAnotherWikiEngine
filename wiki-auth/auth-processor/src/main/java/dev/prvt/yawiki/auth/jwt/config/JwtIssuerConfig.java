package dev.prvt.yawiki.auth.jwt.config;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.infra.AccessTokenGeneratorImpl;
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

}

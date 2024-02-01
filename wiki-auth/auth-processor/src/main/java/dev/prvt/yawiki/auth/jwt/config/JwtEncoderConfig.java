package dev.prvt.yawiki.auth.jwt.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtEncoderConfig {

    @Bean
    public JwtEncoder jwtEncoder(
        @Qualifier("jwtEncoderJWKSource") JWKSource<SecurityContext> jwkSource
    ) {
        return new NimbusJwtEncoder(jwkSource);
    }

}

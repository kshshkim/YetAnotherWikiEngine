package dev.prvt.yawiki.auth.jwt.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Slf4j
@Configuration
public class JwtEncoderJWKSourceConfig {

    @SneakyThrows
    @Bean("jwtEncoderJWKSource")
    @ConditionalOnProperty(
        value = "yawiki.jwt.auth.jwk.source",
        havingValue = "url"
    )
    public JWKSource<SecurityContext> jwkUrlSourceFromUrl(
        @Value("${yawiki.jwt.auth.jwk.url}")
        String jwkUrl
    ) {
        if (jwkUrl.startsWith("http:")) {
            log.warn("Fetching private jwk from unprotected endpoint! (plain http) url: {}", jwkUrl);
        }
        return new RemoteJWKSet<>(new URL(jwkUrl));
    }

    @Bean("jwtEncoderJWKSource")
    @ConditionalOnProperty(
        value = "yawiki.jwt.auth.jwk.source",
        havingValue = "file-path",
        matchIfMissing = true
    )
    public JWKSource<SecurityContext> jwkUrlSourceFromFilePath(
        @Value("${yawiki.jwt.auth.jwk.file-path}")
        String jwkFilePath
    ) throws IOException, ParseException {
        JWKSet jwkSet = JWKSet.load(ResourceUtils.getFile(jwkFilePath));
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

}

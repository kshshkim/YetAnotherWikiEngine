package dev.prvt.yawiki.config.jwt;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import java.security.interfaces.RSAPublicKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.ResourceUtils;

/**
 * 리소스 서버용 JwtDecoder. public 키를 프로퍼티 값에 따라 http url 에서 불러오거나, 지정된 파일 경로에서 불러옴.
 */
@Slf4j
@Configuration
public class ResourceServerConfig {

    /**
     * jwk(json) 파일을 읽어와 JwtDecoder 를 구성.
     *
     * @param jwkUrl http(s) url.
     * @return {@link NimbusJwtDecoder}
     */
    @Bean("YawikiResourceServerJwtDecoder")
    @ConditionalOnProperty(
        value = "yawiki.jwt.resource.jwk.source",
        havingValue = "url"
    )
    public JwtDecoder jwtDecoderFromURL(
        @Value("${yawiki.jwt.resource.jwk.url}")
        String jwkUrl
    ) {
        if (jwkUrl.startsWith("http:")) {
            log.warn("Fetching public jwk from unprotected endpoint! (plain http) url: {}", jwkUrl);
        }
        return NimbusJwtDecoder.withJwkSetUri(jwkUrl).build();
    }

    /**
     * jwk(json) 파일을 읽어와 JwtDecoder 를 구성.
     *
     * @param jwkFilePath jwk 파일의 경로. classpath, file, 혹은 일반 경로명 모두 사용 가능.
     * @param keyId       jwk 파일에 포함된 키 중, 사용할 키의 ID
     * @return {@link NimbusJwtDecoder}
     */
    @Bean("YawikiResourceServerJwtDecoder")
    @ConditionalOnProperty(
        value = "yawiki.jwt.resource.jwk.source",
        havingValue = "file-path",
        matchIfMissing = true
    )
    @SneakyThrows
    public JwtDecoder jwtDecoderFromFilePath(
        @Value("${yawiki.jwt.resource.jwk.file-path}") String jwkFilePath,
        @Value("${yawiki.jwt.resource.jwk.key-id}") String keyId
    ) {
        JWKSet jwkSet = JWKSet.load(ResourceUtils.getFile(jwkFilePath));
        return NimbusJwtDecoder
                   .withPublicKey(getPublicKey(jwkSet, keyId))
                   .build();
    }

    /**
     * JWKSet 에서 keyId 와 일치하는 JWK 를 기반으로 RSAPublicKey 를 추출함.
     * 일치하는 키가 없는 경우, 맨 첫번째 JWK 를 기반으로 추출함.
     */
    private RSAPublicKey getPublicKey(JWKSet jwkSet, String keyId) throws JOSEException {
        JWK key = jwkSet.getKeyByKeyId(keyId);

        if (key == null) {
            log.warn(
                "cannot find any JWK with matching keyId(kid): '{}', will try finding other key from the set.",
                keyId
            );

            key = jwkSet.getKeys().stream()
                      .findFirst()
                      .orElseThrow(() -> new IllegalStateException("cannot find any JWK from JWKSet."));
        }

        log.info("JWK found. kid: '{}'", key.getKeyID());

        return key.toPublicJWK()
                   .toRSAKey()
                   .toRSAPublicKey();
    }


}


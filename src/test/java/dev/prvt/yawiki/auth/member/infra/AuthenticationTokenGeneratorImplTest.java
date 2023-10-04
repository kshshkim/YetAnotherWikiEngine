package dev.prvt.yawiki.auth.member.infra;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import dev.prvt.yawiki.auth.member.infra.AuthenticationTokenGeneratorImpl;
import dev.prvt.yawiki.auth.member.infra.PasswordHasherImpl;
import dev.prvt.yawiki.config.springsecurity.JwtProperties;
import dev.prvt.yawiki.auth.member.domain.BaseMember;
import dev.prvt.yawiki.auth.member.domain.Member;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTokenGeneratorImplTest {

    AuthenticationTokenGeneratorImplTest() throws NoSuchAlgorithmException {
    }

    @SneakyThrows
    public KeyPair keyPair() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public RSAKey rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (((jwkSelector, context) -> jwkSelector.select(jwkSet)));
    }

    @SneakyThrows
    public JwtDecoder jwtDecoder(RSAKey rsaKey) {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }

    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    RSAKey rsaKey;
    JWKSource<SecurityContext> jwkSource;
    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;
    AuthenticationTokenGeneratorImpl authenticationTokenGenerator;
    BaseMember givenMember;

    PasswordHasherImpl passwordHasher = new PasswordHasherImpl(new BCryptPasswordEncoder());

    JwtProperties jwtProperties;

    @BeforeEach
    void init() {
        Random random = new Random();

        rsaKey = rsaKey(keyPair());
        jwkSource = jwkSource(rsaKey);
        jwtEncoder = jwtEncoder(jwkSource);
        jwtDecoder = jwtDecoder(rsaKey);
        jwtProperties = JwtProperties.builder()
                .issuer(randString())
                .subject(randString())
                .lifespan(random.nextInt(180, 1800))
                .build();
        authenticationTokenGenerator = new AuthenticationTokenGeneratorImpl(jwtEncoder, jwtProperties);
        givenMember = Member.create(UUID.randomUUID(), randString(), randString(), passwordHasher);
    }

    @Test
    void create() {
        // when
        String s = authenticationTokenGenerator.create(givenMember);

        // then
        Jwt decoded = jwtDecoder.decode(s);
        Map<String, Object> claims = decoded.getClaims();
        String contributorId = (String) claims.get("contributorId");
        String name = (String) claims.get("name");
        String issuer = (String) claims.get("iss");
        String subject = decoded.getSubject();

        // yawiki authentication
        assertThat(contributorId)
                .isEqualTo(givenMember.getId().toString());
        assertThat(name)
                .isEqualTo(givenMember.getDisplayedName());

        // properties
        assertThat(decoded.getExpiresAt().minusSeconds(jwtProperties.getLifespan()))
                .isNotNull()
                .isEqualTo(decoded.getIssuedAt());
        assertThat(issuer)
                .isEqualTo(jwtProperties.getIssuer());
        assertThat(subject)
                .isEqualTo(jwtProperties.getSubject());
    }
}
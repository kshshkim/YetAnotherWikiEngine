package dev.prvt.yawiki.auth.member.infra;

import dev.prvt.yawiki.auth.member.domain.BaseMember;
import dev.prvt.yawiki.auth.member.domain.Member;
import dev.prvt.yawiki.config.springsecurity.JwtProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.JwtFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTokenGeneratorImplTest {
//    @SneakyThrows
//    public KeyPair keyPair() {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048);
//        return keyPairGenerator.generateKeyPair();
//    }
//
//    public RSAKey rsaKey(KeyPair keyPair) {
//        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
//                .privateKey(keyPair.getPrivate())
//                .keyID(UUID.randomUUID().toString())
//                .build();
//    }
//
//    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        return (((jwkSelector, context) -> jwkSelector.select(jwkSet)));
//    }
//
//    @SneakyThrows
//    public JwtDecoder jwtDecoder(RSAKey rsaKey) {
//        return NimbusJwtDecoder
//                .withPublicKey(rsaKey.toRSAPublicKey())
//                .build();
//    }
//
//    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
//        return new NimbusJwtEncoder(jwkSource);
//    }

    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;
    AuthenticationTokenGeneratorImpl authenticationTokenGenerator;
    BaseMember givenMember;

    PasswordHasherImpl passwordHasher = new PasswordHasherImpl(new BCryptPasswordEncoder());

    JwtProperties jwtProperties;

    @SneakyThrows
    @BeforeEach
    void init() {
        Random random = new Random();

        jwtEncoder = getJwtEncoder();
        jwtDecoder = getJwtDecoder();
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
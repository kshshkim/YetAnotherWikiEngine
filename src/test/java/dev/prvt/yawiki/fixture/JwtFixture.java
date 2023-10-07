package dev.prvt.yawiki.fixture;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

public class JwtFixture {

    private static volatile KeyPair keyPairInstance;
    private static volatile RSAKey rsaKeyInstance;
    private static volatile JWKSource<SecurityContext> jwkSourceInstance;
    private static volatile JwtDecoder jwtDecoderInstance;
    private static volatile JwtEncoder jwtEncoderInstance;

    private JwtFixture() {
        // private constructor to prevent instantiation
    }

    public static KeyPair getKeyPair() throws NoSuchAlgorithmException {
        if (keyPairInstance == null) {
            synchronized (JwtFixture.class) {
                if (keyPairInstance == null) {
                    keyPairInstance = _keyPair();
                }
            }
        }
        return keyPairInstance;
    }

    public static RSAKey getRSAKey() throws NoSuchAlgorithmException {
        if (rsaKeyInstance == null) {
            synchronized (JwtFixture.class) {
                if (rsaKeyInstance == null) {
                    rsaKeyInstance = _rsaKey(getKeyPair());
                }
            }
        }
        return rsaKeyInstance;
    }

    public static JWKSource<SecurityContext> getJWKSource() throws NoSuchAlgorithmException {
        if (jwkSourceInstance == null) {
            synchronized (JwtFixture.class) {
                if (jwkSourceInstance == null) {
                    jwkSourceInstance = _jwkSource(getRSAKey());
                }
            }
        }
        return jwkSourceInstance;
    }

    public static JwtDecoder getJwtDecoder() throws NoSuchAlgorithmException, JOSEException {
        if (jwtDecoderInstance == null) {
            synchronized (JwtFixture.class) {
                if (jwtDecoderInstance == null) {
                    jwtDecoderInstance = _jwtDecoder(getRSAKey());
                }
            }
        }
        return jwtDecoderInstance;
    }

    public static JwtEncoder getJwtEncoder() throws NoSuchAlgorithmException {
        if (jwtEncoderInstance == null) {
            synchronized (JwtFixture.class) {
                if (jwtEncoderInstance == null) {
                    jwtEncoderInstance = _jwtEncoder(getJWKSource());
                }
            }
        }
        return jwtEncoderInstance;
    }

    // Original methods remain private:
    private static KeyPair _keyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private static RSAKey _rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static JWKSource<SecurityContext> _jwkSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    private static JwtDecoder _jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }

    private static JwtEncoder _jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}

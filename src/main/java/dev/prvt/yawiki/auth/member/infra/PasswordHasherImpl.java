package dev.prvt.yawiki.auth.member.infra;

import dev.prvt.yawiki.auth.member.domain.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password Hasher Adapter
 */
@Component
@RequiredArgsConstructor
public class PasswordHasherImpl implements PasswordHasher {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public String hash(String toHash) {  // todo hash password
        return bCryptPasswordEncoder.encode(toHash);
    }

    @Override
    public boolean matches(String raw, String hashed) {
        return bCryptPasswordEncoder.matches(raw, hashed);
    }
}

package dev.prvt.yawiki.member.infra;

import dev.prvt.yawiki.member.domain.PasswordHasher;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Password Hasher Adapter
 */
@Component
public class PasswordHasherImpl implements PasswordHasher {

    @Override
    public String hash(String toHash) {
        return BCrypt.hashpw(toHash, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}

package dev.prvt.yawiki.member.domain;

import dev.prvt.yawiki.member.exception.PasswordMismatchException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Getter
@Table(name = "regular_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseMember {
    private String username;
    private String password;

    private void updatePassword(String raw, PasswordHasher hasher) {
        this.password = hasher.hash(raw);
    }

    public void updatePassword(
        String oldPassword,
        String newPassword,
        PasswordHasher hasher
    ) {
        verifyPassword(oldPassword, hasher);
        updatePassword(newPassword, hasher);
    }

    public void verifyPassword(String raw, PasswordHasher hasher) {
        if (!hasher.matches(raw, password)) {
            throw new PasswordMismatchException();
        }
    }

    protected Member(UUID id, String username, String password) {
        super(id, username);
        this.username = username;
        this.password = password;
    }

    static public Member create(UUID uuid, String username, String password, PasswordHasher hasher) {
        return new Member(uuid, username, hasher.hash(password));
    }
}

package dev.prvt.yawiki.core.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Getter
@Table(name = "regular_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseMember {
    private String username;
    private String password;

    public void updatePassword(String raw, PasswordHasher hasher) {
        this.password = hasher.hash(raw);
    }

    public void validatePassword(String raw, PasswordHasher hasher) {
        String hashed = hasher.hash(raw);
        if (!hashed.equals(password)) {
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
package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenExpirationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;

/**
 * id 는 지속되는 고유 값. 회원은 여러개의 RefreshToken 을 가질 수 있음. <br>
 * token 필드는 일회용으로, 재발급시마다 재생성함.
 */
@Entity
@Getter
@Table(name = "refresh_token", indexes = {@Index(name = "idx__refresh_token__token", columnList = "token")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "refresh_token_id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "token", columnDefinition = "BINARY(16)")
    private UUID token;
    @Column(name = "issued_to_id", columnDefinition = "BINARY(16)")
    private UUID issuedToId;
    @Column(name = "issued_to_name")
    private String issuedToName;
    @Column(name = "issued_at")
    private LocalDateTime issuedAt;
    @Column(name = "renewed_at")
    private LocalDateTime renewedAt;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    private void validateExpiration() {
        if (this.expiresAt.isBefore(LocalDateTime.now())) {
            throw RefreshTokenExpirationException.INSTANCE;
        }
    }

    private void regenToken() {
        this.token = UUID.randomUUID();
    }

    public void renew(long expirationInMinutes) {
        validateExpiration();
        renewedAt = LocalDateTime.now();
        expiresAt = renewedAt.plusMinutes(expirationInMinutes);
        regenToken();
    }

    public String getTokenValue() {
        return token.toString();
    }

    @Builder
    protected RefreshToken(UUID id, UUID token, UUID issuedToId, String issuedToName, LocalDateTime issuedAt, LocalDateTime renewedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.token = token;
        this.issuedToId = issuedToId;
        this.issuedToName = issuedToName;
        this.issuedAt = issuedAt;
        this.renewedAt = renewedAt;
        this.expiresAt = expiresAt;
    }

    static public RefreshToken create(UUID issuedTo, String issuedToName, long expirationInMinutes) {
        LocalDateTime now = LocalDateTime.now();
        return RefreshToken.builder()
                .token(UUID.randomUUID())
                .issuedToId(issuedTo)
                .issuedToName(issuedToName)
                .issuedAt(now)
                .renewedAt(now)
                .expiresAt(now.plusMinutes(expirationInMinutes))
                .build();
    }
}

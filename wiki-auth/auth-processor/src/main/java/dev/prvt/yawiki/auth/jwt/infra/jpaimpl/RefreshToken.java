package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import static java.util.Objects.requireNonNull;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenException;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import dev.prvt.yawiki.common.util.jpa.uuid.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * id 는 지속되는 고유 값. 회원은 여러개의 RefreshToken 을 가질 수 있음. <br> token 필드는 일회용으로, 재발급시마다 재생성함.
 */
@Entity
@Getter
@Table(name = "refresh_token", indexes = {
    @Index(name = "idx__refresh_token__token", columnList = "token")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @UuidV7Generator
    @Column(name = "refresh_token_id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "token", columnDefinition = "BINARY(16)")
    private UUID token;
    @Column(name = "sub_id", columnDefinition = "BINARY(16)")
    private UUID subjectId;
    @Column(name = "sub_name")
    private String subjectName;
    @Column(name = "issued_at")
    private Instant issuedAt;
    @Column(name = "renewed_at")
    private Instant renewedAt;
    @Column(name = "expires_at")
    private Instant expiresAt;

    public RefreshToken renew(String subjectName, long expirationInSeconds, Instant currentTime) {
        verify(subjectName, currentTime);

        renewedAt = currentTime;
        expiresAt = renewedAt.plusSeconds(expirationInSeconds);

        regenerateTokenValue();
        return this;
    }

    public RefreshToken verify(String subjectName, Instant currentTime) {
        verifySubjectName(subjectName);
        verifyExpiration(currentTime);
        return this;
    }

    public String getTokenValue() {
        return token.toString();
    }

    private void verifyExpiration(Instant currentTime) {
        if (this.expiresAt.isBefore(currentTime)) {
            throw RefreshTokenException.EXPIRED;
        }
    }

    private void verifySubjectName(String subjectName) {
        if (!this.subjectName.equals(subjectName)) {
            throw new RefreshTokenException("subject name mismatch");
        }
    }

    private void regenerateTokenValue() {
        this.token = UUID.randomUUID();
    }

    @Builder
    protected RefreshToken(
        UUID id,
        UUID token,
        UUID subjectId,
        String subjectName,
        Instant issuedAt,
        Instant renewedAt,
        Instant expiresAt
    ) {
        this.id = id;
        this.token = token;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.issuedAt = issuedAt;
        this.renewedAt = renewedAt;
        this.expiresAt = expiresAt;
    }

    public RefreshTokenRecord mapToRecord() {
        return new RefreshTokenRecord(
            this.getTokenValue(),
            this.subjectId,
            this.subjectName
        );
    }

    static public RefreshToken create(
        UUID issuedTo,
        String issuedToName,
        long expirationInSeconds,
        Instant currentTime
    ) {
        requireNonNull(issuedTo);
        requireNonNull(issuedToName);
        requireNonNull(currentTime);

        return RefreshToken.builder()
                   .token(UUID.randomUUID())
                   .subjectId(issuedTo)
                   .subjectName(issuedToName)
                   .issuedAt(currentTime)
                   .renewedAt(currentTime)
                   .expiresAt(currentTime.plusSeconds(expirationInSeconds))
                   .build();
    }
}

package dev.prvt.yawiki.core.contributor.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * <p>Member, 권한 관련 엔티티와 동일한 ID 를 공유함. 특성상 그렇게 큰 영향을 줄 것 같진 않지만, 쿼리 성능을 위해서 persistable 인터페이스 구현을 고려할것.</p>
 */
@Getter
@Entity
@Table(name = "contributor")
@DiscriminatorColumn(name = "dtype")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Contributor {
    @Id
    @Column(name = "contributor_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    protected ContributorState state;

    public abstract String getName();
    public Contributor(UUID id, ContributorState state) {
        this.id = id;
        this.state = state;
    }
}

package dev.prvt.yawiki.core.wikipage.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;


/**
 * Revision 은 한 번 DB 에 저장된 후에는 수정될 일이 없음.
 * 문서를 수정할 경우 새로운 Rev 를 생성함.
 * documentId + revVersion 에 unique 제약조건을 걸어서 낙관적 락 처럼 사용함.
 */
@Entity
@Getter
@Table(
        name = "revision",
        indexes = {
                @Index(
                        name = "idx__revision__page_id__rev_version",
                        columnList = "page_id, rev_version",
                        unique = true),
                @Index(
                        name = "idx__revision__contributor_id",
                        columnList = "contributor_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Revision {
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "rev_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "contributor_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID contributorId;

    @Column(name = "rev_version", nullable = false, updatable = false)
    private Integer revVersion;  // JPA 낙관적 락의 버전이 아님.
    /**
     * <p>참조무결성 제약조건이 설정되는 경우, MySQL 등 일부 DBMS에서 트랜잭션 시점 충돌 발생시 예상치 못한 데드락이 감지됨.</p>
     * <ol>
     *     <li>Revision 엔티티가 삽입될 때, 참조 무결성 제약조건으로 인해서 WikiPage에 복수의 공유 락이 걸림.</li>
     *     <li>복수의 트랜잭션이 모두 WikiPage에 공유락을 건 상태로, WikiPage에 대한 배타락을 확보하기 위해 대기함.(update시 배타락 필요)</li>
     *     <li>전형적인 순환대기 상황이 형성되어 데드락이 발생함. (공유락이 걸린 대상에는 배타락을 걸 수 없음)</li>
     * </ol>
     * <p>보통은 바로 데드락 상황이 감지되어 DBMS에서 이를 잘 처리하지만, 데드락이 발생하는 상황 자체가 이상적이지 않기 때문에 참조무결성 제약조건을 없애는 것을 권장함.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private WikiPage wikiPage;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)  // 마찬가지로 수정될 일이 거의 없음.
    @JoinColumn(name = "raw_content", nullable = false, updatable = false)
    private RawContent rawContent;

    @Column(name = "comment", nullable = false, updatable = false)
    private String comment;

    @Column(name = "diff", nullable = false, updatable = false)
    private Integer diff;  // 이전 버전과의 크기 차이

    @Column(name = "size", nullable = false, updatable = false)
    private int size;

    /**
     * 한 번 생성된 Revision 은 변경될 일이 없기 때문에 EntityListener 활용하지 않음.
     */
    private LocalDateTime timestamp;

    public String getContent() {
        boolean hasContent = (this.rawContent != null) && (this.size > 0);
        return hasContent ? this.rawContent.getContent() : "";
    }

    /**
     * <p>생성 시점에만 호출됨. 생성 시점에 beforeRevision 이 할당되지 않으면 첫 Revision 으로 간주.</p>
     *
     * @param beforeRev 이전 Revision
     */
    private void asAfter(Revision beforeRev) {
        if (this.revVersion != null) {
            throw new IllegalStateException("cannot change finalized field: revVersion=" + this.revVersion);
        }
        this.revVersion = getNewVersion(beforeRev);
        this.diff = getDiff(beforeRev);
    }

    private Integer getDiff(Revision beforeRev) {
        return beforeRev == null ? this.getSize() : this.getSize() - beforeRev.getSize();
    }

    private int getNewVersion(Revision beforeRev) {
        return beforeRev == null ? 1 : beforeRev.getRevVersion() + 1;
    }

    @Builder
    protected Revision(@NotNull UUID contributorId,  // 생성 시점에 반드시 필요함.
                       @NotNull String comment,  // 생성 시점에 반드시 필요함.
                       @NotNull WikiPage wikiPage,  // 생성 시점에 반드시 필요함.
                       RawContent rawContent,  // rawContent 는 null 일 수 있음. (삭제된 문서 등)
                       Revision beforeRevision,  // 첫 Revision 일 경우 null 값이 들어올 수 있음.
                       LocalDateTime timestamp  //
    ) {
        this.contributorId = contributorId;
        this.rawContent = rawContent;
        this.size = rawContent == null ? 0 : rawContent.getSize();
        this.comment = comment;
        this.wikiPage = wikiPage;
        this.asAfter(beforeRevision);
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
    }
}

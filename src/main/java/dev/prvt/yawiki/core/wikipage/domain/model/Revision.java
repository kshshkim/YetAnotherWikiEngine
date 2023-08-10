package dev.prvt.yawiki.core.wikipage.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
    private long revVersion;  // JPA 낙관적 락의 버전이 아님.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false, updatable = false)
    private WikiPage wikiPage;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)  // 마찬가지로 수정될 일이 거의 없음.
    @JoinColumn(name = "raw_content", nullable = false, updatable = false)
    private RawContent rawContent;

    @Column(name = "comment", nullable = false, updatable = false)
    private String comment;

    public String getContent() {
        return this.rawContent == null ? "" :
                rawContent.getContent();
    }

    public void setRevVersionAfter(Revision beforeRev) {
        if (this.revVersion != 0L) {
            throw new IllegalStateException("cannot change finalized field: revVersion="+this.revVersion);
        }
        this.revVersion = getNewVersion(beforeRev);
    }

    public void setRawContent(RawContent rawContent) {
        if (this.rawContent != null) {
            throw new IllegalStateException("cannot change finalized field: rawContent");
        }
        this.rawContent = rawContent;
    }

    private long getNewVersion(Revision beforeRev) {
        return beforeRev == null ? 1L : beforeRev.getRevVersion() + 1L;
    }

    @Builder
    protected Revision(UUID contributorId, WikiPage wikiPage, RawContent rawContent, String comment) {
        this.contributorId = contributorId;
        this.wikiPage = wikiPage;
        this.rawContent = rawContent;
        this.comment = comment;
    }
}

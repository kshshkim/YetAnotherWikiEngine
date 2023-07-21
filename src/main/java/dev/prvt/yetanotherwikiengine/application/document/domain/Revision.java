package dev.prvt.yetanotherwikiengine.application.document.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * Revision 은 한 번 DB 에 저장된 후에는 수정될 일이 없음.
 * 문서를 수정할 경우 새로운 Rev 를 생성함.
 * documentId + revVersion 에 unique 제약조건을 걸어서 낙관적 락 처럼 사용함.
 */
@Entity
@Getter
@Table(
        name = "REVISION",
        indexes = @Index(
                name = "idx__revision__document_id__rev_version",
                columnList = "document_id, rev_version",
                unique = true))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Revision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rev_version", updatable = false)
    private long revVersion;  // JPA 낙관적 락의 버전이 아님.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", updatable = false)
    private Document document;

    @Getter(AccessLevel.NONE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)  // 마찬가지로 수정될 일이 거의 없음.
    @JoinColumn(name = "raw_content", updatable = false)
    private RawContent rawContent;

    private String comment;

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

    public Revision(Document document, String comment) {
        this.document = document;
        this.comment = comment;
    }
}

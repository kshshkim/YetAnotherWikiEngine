package dev.prvt.yetanotherwikiengine.application.document.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "DOCUMENT",
        indexes = {
                @Index(
                        name = "idx__document_title",
                        columnList = "title",
                        unique = true
                )})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Document {
    @Id
    @Column(name = "document_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique 제약조건 설정됨.
    private String title;

    // InnerLink 생성을 위해서 만들어진 Document 엔티티의 경우, 참조를 위해 생성된 것인지, 실제로 생성된 문서인지를 알 수 있어야함.
    @Column(name = "is_active")
    private boolean isActive;

    // 수정 충돌 방지를 위해서 사용되는 토큰. 수정 요청에 대해서 항상 일치하는지 검증해야함.
    // 위키의 특성상 트랜잭션이나 낙관적 락의 범위에서 해결되지 않는 문제들이 존재함.
    // sequential 한 버전 정보를 사용하지 않는 이유는 악의적인 사용자가 충돌 방지책을 우회하는데 이용할 수 있기 때문임.
    // 문서의 권한 테이블을 따로 빼는 경우, 관심사가 다른 editToken 을 다른 테이블로 빼는 것도 생각해볼 수 있음.
    private String editToken;

    // 현재 버전.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) // 새 버전을 할당하면 알아서 저장돼야함. 리비전은 거의 수정되지 않으므로 그 이상의 cascade 는 필요 없음.
    @JoinColumn(name = "current_revision_id")
    private Revision currentRevision;  // todo Revision.document 필드를 통해서 양방향 참조가 일어나고 있는데, 바람직하지 않아보임. 다른 방법으로 해결할것.

    @OneToMany(mappedBy = "referer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentReference> innerLinks = new ArrayList<>();

    // 무작위 UUID 를 사용하나, 변경 가능함.
    private void updateEditToken() {
        this.editToken = UUID.randomUUID().toString();
    }

    private void replaceCurrentRevisionWith(Revision revision) {
        revision.setRevVersionAfter(this.currentRevision);
        this.currentRevision = revision;
    }

    // 검증 로직과 reference setting 로직을 분리하였음.
    // DocumentReference 등, 다른 클래스에 대한 책임을 지지 않음.
    public void updateDocument(Revision newRevision) {
        replaceCurrentRevisionWith(newRevision);
        this.isActive = true;
    }

    public Document(String title, boolean isActive, Revision currentRevision) {
        this.title = title;
        this.isActive = isActive;
        this.currentRevision = currentRevision;
    }

    public static Document create(String title) {
        Document document = new Document(title, false, null);
        document.updateEditToken();
        return document;
    }
}

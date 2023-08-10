package dev.prvt.yawiki.core.wikipage.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;

/**
 * <h2>WikiPage</h2>
 * <p>위키의 문서 도메인 엔티티로, {@link dev.prvt.yawiki.core.wikipage.domain} 애그리거트의 Root 역할을 함.</p>
 * <p>문서 생성과 조회, 업데이트 이외의 부가적인 책임(권한 검증, 마크다운 파싱, 내부 레퍼런스 업데이트 등)은 도메인 객체의 바깥에 있음.</p>
 * <p>적절하게 InnerReference 업데이트가 이루어질 수 있도록, 모든 업데이트 작업은 도메인 서비스를 통해 이루어져야함.</p>
 * @see dev.prvt.yawiki.core.wikipage.domain.WikiPageDomainService
 */

@Entity
@Table(
        name = "wiki_page",
        indexes = {
                @Index(
                        name = "idx__page_title",
                        columnList = "title",
                        unique = true
                )})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WikiPage {
    static private final UUID DEFAULT_GROUP_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "page_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "owner_group_id", columnDefinition = "BINARY(16)")
    private UUID ownerGroupId;

    /**
     * <p>트랜잭션 시점에서의 충돌 처리를 위해 낙관적 락을 사용하며, 그 외에는 사용되지 않는 필드임.</p>
     * <p>편집 시작 시점에서의 충돌에 대해서 별개의 토큰을 사용하는 이유는 다음과 같음.</p>
     * <p>(1) 편집 충돌에서 우위를 점하기 위해 예측 가능한 값을 악용할 수 있고,
     * (2) JPA 에서 관리하는 값으로, 직접 수정할 수 없기 때문에 별도의 토큰을 사용하여야함.</p>
     */
    @Version
    private int version;

    /**
     * <p>위키의 특성상 편집 충돌 판정 시점은 트랜잭션이 시작하기 이전, 편집자가 수정을 시작한 시점이어야함.</p>
     * <p>수정 충돌 방지를 위해서 무작위 생성 토큰을 사용함. 업데이트시마다 검증해야하며, 업데이트가 성공적으로 이루어지면 값을 재생성해야함.</p>
     * <p>sequential 한 버전 정보를 사용하지 않는 이유는 악의적인 사용자가 충돌 방지책을 우회하는데 이용할 수 있기 때문임.</p>
     * <p>구조적 유연성을 위해 편집 시작 시점에서의 충돌 판정 로직은 도메인 객체 바깥에서 구현하도록 함.</p>
     */
    private String versionToken;

    /**
     * 문서 제목. PK 는 외부로 노출하지 않도록 함.
     */
    private String title;

    /**
     * 문서를 삭제하거나, 처음 생성되어 내용이 없는 경우 false
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * <p>모든 Revision 은 WikiPage 에 대해서 ManyToOne 관계를 가짐.</p>
     * <p>WikiPage 는 현재 대표 Revision 에 대해서 OneToOne 관계를 가짐.</p>
     * <p>양방향 참조가 일어나고 있지만, 두 참조의 의미가 다르고, Revision 은 immutable 한 엔티티이기 때문에 side effect 나 관리 비용 증가 문제는 없을 것으로 생각됨.</p>
     * @see Revision
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "current_revision_id")
    private Revision currentRevision;

    /**
     * <p>객체 그래프를 타고 현재 content 를 반환함.</p>
     * @return 렌더링 되지 않은 본문
     */
    public String getContent() {
        return this.currentRevision == null ? "" : this.currentRevision
                .getContent();
    }

    /**
     * 편집 성공시 버전 토큰을 재생성함.
     */
    private void updateVersionToken() {
        this.versionToken = UUID.randomUUID().toString();
    }

    /**
     * @param revision currentRevision 이 될 새로운 Revision 객체. 현재 Revision 의 다음 번호를 할당받음.
     * @see Revision
     */
    private void replaceCurrentRevisionWith(Revision revision) {
        revision.setRevVersionAfter(this.currentRevision);
        this.currentRevision = revision;
    }

    /**
     * @param newRevision 새로 조립되어 영속화되지 않은 Revision 객체
     */
    public void update(Revision newRevision) {
        replaceCurrentRevisionWith(newRevision);
        this.isActive = true;
        updateVersionToken();
    }

    /**
     * Root Aggregate 로써 Revision 엔티티의 생성과 영속화에 대한 책임이 포함된 update
     * @param contributorId 수정자 ID
     * @param comment 수정 코멘트
     * @param content 마크다운 문법에 따라서 파싱되지 않은 순수 문자열 본문
     */
    public void update(UUID contributorId, String comment, String content) {
        Revision newRev = buildNewRevision(contributorId, comment, content);
        update(newRev);
    }

    private Revision buildNewRevision(UUID contributorId, String comment, String content) {
        return Revision.builder()
                .wikiPage(this)
                .contributorId(contributorId)
                .comment(comment)
                .rawContent(new RawContent(content))
                .build();
    }

    private WikiPage(String title, UUID ownerGroupId, boolean isActive, Revision currentRevision) {
        this.title = title;
        this.ownerGroupId = ownerGroupId;
        this.isActive = isActive;
        this.currentRevision = currentRevision;
    }

    public static WikiPage create(String title) {
        return WikiPage.create(title, DEFAULT_GROUP_ID);
    }

    public static WikiPage create(String title, UUID ownerGroupId) {
        WikiPage created = new WikiPage(title, ownerGroupId, false, null);
        created.updateVersionToken();
        return created;
    }
}

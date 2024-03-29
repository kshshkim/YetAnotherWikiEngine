package dev.prvt.yawiki.core.wikipage.domain.model;

import dev.prvt.yawiki.common.util.jpa.uuid.UuidV7Generator;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageRenameException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <h2>WikiPage</h2>
 * <p>위키의 문서 도메인 엔티티로, {@link dev.prvt.yawiki.core.wikipage.domain} 애그리거트의 Root 역할을 함.</p>
 * <p>문서 생성과 조회, 업데이트 이외의 부가적인 책임(권한 검증, 마크다운 파싱, 내부 레퍼런스 업데이트 등)은 도메인 객체의 바깥에 있음.</p>
 * <p>적절하게 InnerReference 업데이트가 이루어질 수 있도록, 모든 업데이트 작업은 도메인 서비스를 통해 이루어져야함.</p>
 * <p><b>엔티티 내부의 필드를 변경하는 모든 작업은 내부적으로 update 메소드를 활용하여 새로운 Revision을 생성해야함.</b></p>
 * <p>
 * <h3>동시성 처리</h3>
 * <p>
 * 트랜잭션 시점의 동시성 처리
 * <ul>
 * <li>트랜잭션 시점의 동시성 처리는 일반적으로 Revision 엔티티에 걸린 유일성 제약조건으로 확보 가능함.(편집 충돌시, 유일성 제약조건으로 인해 insert 쿼리 실패)</li>
 * <li>모든 update 작업은 Revision 엔티티를 새로 생성하여야함.</li>
 * </ul>
 * </p>
 * <p>
 * 편집 시점의 동시성 처리
 * <ul>
 * <li>편집을 시작한 문서 버전이 이전 버전인 경우, 충돌이 일어나고 편집이 실패해야함. 트랜잭션의 범위 내에서 해결 불가능한 문제.</li>
 * <li>편집 시점의 동시성 처리는 versionToken을 기반으로 이루어짐.</li>
 * <li>편집 충돌이 자주 발생하는 상황에 악용될 수 없도록 예측 불가능한 값을 사용함.</li>
 * </ul>
 * </p>
 *
 * </p>
 *
 * @see Revision
 */

@Entity
@Table(
        name = "wiki_page",
        indexes = {
                @Index(
                        name = "idx__wiki_page__title__namespace",
                        columnList = "title, namespace",
                        unique = true
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WikiPage {
    @Id
    @UuidV7Generator
    @Column(name = "page_id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * <p>위키의 특성상 편집 충돌 판정 시점은 트랜잭션이 시작하기 이전, 편집자가 수정을 시작한 시점이어야함.</p>
     * <p>수정 충돌 방지를 위해서 무작위 생성 토큰을 사용함. 업데이트시마다 검증해야하며, 업데이트가 성공적으로 이루어지면 값을 재생성해야함.</p>
     * <p>sequential 한 버전 정보를 사용하지 않는 이유는 악의적인 사용자가 충돌 방지책을 우회하는데 이용할 수 있기 때문임.</p>
     */
    private String versionToken;

    /**
     * 문서 제목. PK 는 외부로 노출하지 않도록 함.
     */
    private String title;

    /**
     * 네임스페이스
     *
     * @see Namespace
     */
    @Column(name = "namespace", columnDefinition = "INTEGER")
    private Namespace namespace;

    /**
     * 문서를 삭제하거나, 처음 생성되어 내용이 없는 경우 false
     */
    @Column(name = "active")
    private boolean active;

    /**
     * 수정으로 인해 문서가 활성화 되었는지 여부. active 가 false 에서 true 로 수정된 경우 true.
     * Transient 이기 때문에 초기값은 false
     */
    @Transient
    private boolean activated;

    /**
     * <p>모든 Revision 은 WikiPage 에 대해서 ManyToOne 관계를 가짐.</p>
     * <p>WikiPage 는 현재 대표 Revision 에 대해서 OneToOne 관계를 가짐.</p>
     * <p>
     * 양방향 참조가 일어나고 있지만, 두 참조의 의미가 다르고, Revision 은 immutable 한 엔티티이기 때문에 side effect 나 관리 비용 증가 문제는 적을 것으로 생각됨.<br>
     * 단, Revision.wikiPage 필드에 참조무결성 제약조건이 설정될 경우, 일부 DBMS에서 데드락 상황이 형성될 수 있음. 참조무결성 제약조
     * </p>
     * <p>
     * 이 필드에 대해서 외래키 제약조건이 걸리더라도, 인덱스가 제대로 잡혀있다면 데드락 상황이 발생하지는 않음.
     * </p>
     *
     * @see Revision
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "current_revision_id")//, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Revision currentRevision;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    /**
     * 마지막 수정자 ID
     */
    @Column(name = "last_modified_by", columnDefinition = "BINARY(16)")
    private UUID lastModifiedBy;

    @Transient
    @Getter(AccessLevel.NONE)
    private WikiPageTitle wikiPageTitle;

    /**
     * <p>객체 그래프를 타고 현재 content 를 반환함.</p>
     *
     * @return 렌더링 되지 않은 본문
     */
    @Transient
    public String getContent() {
        return this.currentRevision == null ? "" : this.currentRevision
                .getContent();
    }

    @Transient
    public WikiPageTitle getWikiPageTitle() {
        if (this.wikiPageTitle == null) {
            this.wikiPageTitle = new WikiPageTitle(this.title, this.namespace);
        }
        return this.wikiPageTitle;
    }

    /**
     * Root Aggregate 로써 Revision 엔티티의 생성과 영속화에 대한 책임이 포함된 update<br>
     *
     * @param contributorId 수정자 ID
     * @param comment       수정 코멘트
     * @param content       마크다운 문법에 따라서 파싱되지 않은 순수 문자열 본문
     */
    public void update(UUID contributorId, String comment, String content) {
        Revision newRev = buildNewRevision(contributorId, comment, content);
        update(newRev);
        activate();
        modified(contributorId);
    }

    /**
     * <p>빈 리비전을 생성하고, 문서의 상태를 삭제됨으로 변경.</p>
     */
    public void deactivate(UUID contributorId, String comment) {
        Revision newRev = buildNewRevision(contributorId, comment, "");
        update(newRev);
        deactivate();
        modified(contributorId);
    }

    /**
     * 문서의 제목을 변경함.
     * <p>변경 후 기존 제목에 대해 리다이렉트를 설정하는 기능은 이 클래스의 책임이 아님.</p>
     *
     * @param contributorId 기여자 ID
     * @param newTitle      새 제목
     * @param comment       수정 코멘트
     */
    public void rename(UUID contributorId, String newTitle, String comment) {
        renameRequireActive();
        renameTitle(newTitle);

        Revision newRev = buildNewRevision(contributorId, comment, getContent());
        update(newRev);
        modified(contributorId);
    }

    /**
     * 제목 변경은 활성화된 문서(존재하는 문서)에 대해서만 수행할 수 있음.
     */
    private void renameRequireActive() {
        if (!isActive()) {
            throw WikiPageRenameException.notActive(this);
        }
    }

    private void renameTitle(String newTitle) {
        this.title = newTitle;
        this.wikiPageTitle = new WikiPageTitle(newTitle, this.namespace);
    }

    /**
     * 편집 성공시 버전 토큰을 재생성함.
     */
    private void updateVersionToken() {
        this.versionToken = UUID.randomUUID().toString();
    }

    void activate() {
        if (!isActive()) {
            this.active = true;
            this.activated = true;
        }
    }

    private void deactivate() {
        this.active = false;
    }

    /**
     * @param revision currentRevision 이 될 새로운 Revision 객체. 현재 Revision 의 다음 번호를 할당받음.
     * @see Revision
     */
    private void replaceCurrentRevisionWith(Revision revision) {
        this.currentRevision = revision;
    }

    /**
     * @param newRevision 새로 조립되어 영속화되지 않은 Revision 객체
     */
    private void update(Revision newRevision) {
        replaceCurrentRevisionWith(newRevision);
        updateVersionToken();
    }

    /**
     * 엔티티의 값을 수정하는 메서드가 실행된 경우 함께 호출되어야함.
     * 테스트 편의를 위해 직접 구현하였으나, AuditingEntityListener 로 구현하는 것이 보다 간단할 것으로 보이며, 유지보수에 유리할 것으로 보임.
     */
    private void modified(UUID contributorId) {
        modified(contributorId, LocalDateTime.now());
    }

    private void modified(UUID contributorId, LocalDateTime modifiedAt) {
        this.lastModifiedAt = modifiedAt == null ? LocalDateTime.now() : modifiedAt;
        this.lastModifiedBy = contributorId;
    }

    private Revision buildNewRevision(UUID contributorId, String comment, String content) {
        return Revision.builder()
                .wikiPage(this)
                .beforeRevision(this.currentRevision)
                .contributorId(contributorId)
                .comment(comment)
                .rawContent(new RawContent(content))
                .build();
    }

    @Builder(access = AccessLevel.PROTECTED)
    protected WikiPage(
            UUID id,
            String versionToken,
            String title,
            Namespace namespace,
            boolean active,
            boolean activated,
            Revision currentRevision,
            LocalDateTime lastModifiedAt,
            UUID lastModifiedBy
    ) {
        this.id = id;
        this.versionToken = versionToken;
        this.title = title;
        this.namespace = namespace;
        this.active = active;
        this.activated = activated;
        this.currentRevision = currentRevision;

        modified(lastModifiedBy, lastModifiedAt);
        updateVersionToken();
    }
}

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
 * <p><b>엔티티 내부의 필드를 변경하는 모든 작업은 내부적으로 update 메소드를 활용하여 새로운 Revision을 생성해야함.</b></p>
 * <p>
 *     <h3>동시성 처리</h3>
 * <p>
 * 트랜잭션 시점의 동시성 처리
 * <ul>
 * <li>트랜잭션 시점의 동시성 처리는 일반적으로 Revision 엔티티에 걸린 유니크 제약조건으로 확보 가능함. (낙관적 락과 유사하게 작동함)</li>
 * <li>엔티티 내부의 필드에 변화가 생기는 모든 작업은 Revision 엔티티를 새로 생성하여야함.</li>
 * <li>규칙이 지켜진다면 낙관적 락은 제거되어도 무방하지만(H2, MySQL), 만일을 위해 남겨둠.</li>
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
 * @see dev.prvt.yawiki.core.wikipage.domain.WikiPageDomainService
 * @see Revision
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
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "page_id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * <p>JPA 에서 관리하는 낙관락에 사용되는 필드</p>
     * <p>update 작업시 충돌이 일어날 경우, 보통 Revision 엔티티에 걸린 unique 제약조건(wiki_page_id, rev_version)으로 인해 트랜잭션이 실패함.</p>
     * <p>이 엔티티 내부의 모든 변경 작업은 update 메소드를 이용해서 새로운 Revision 을 생성하도록 작동해야함. 이 규칙이 잘 지켜질 경우 낙관락은 없어도 무방하나 만일을 위해 남겨둠.</p>
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

    /**
     * <p>객체 그래프를 타고 현재 content 를 반환함.</p>
     *
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
        this.currentRevision = revision;
    }

    /**
     * @param newRevision 새로 조립되어 영속화되지 않은 Revision 객체
     */
    private void update(Revision newRevision) {
        replaceCurrentRevisionWith(newRevision);
        this.isActive = true;
        updateVersionToken();
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
    }


    /**
     * <p>빈 리비전을 생성하고, 문서의 상태를 삭제됨으로 변경.</p>
     */
    public void delete(UUID contributorId, String comment) {
        update(contributorId, comment, "");
        this.isActive = false;
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

    private WikiPage(String title, boolean isActive, Revision currentRevision) {
        this.title = title;
        this.isActive = isActive;
        this.currentRevision = currentRevision;
    }

    public static WikiPage create(String title) {
        WikiPage created = new WikiPage(title, false, null);
        created.updateVersionToken();
        return created;
    }
}

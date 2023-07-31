package dev.prvt.yawiki.application.domain.innerreference;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
// 복합키 고려할것. clustered index 사용시 읽기 성능에 유리할 것으로 보임. insert 시 성능이 어떨지는 알아봐야함.
@Entity
@Getter
@Table(
        name = "INNER_REFERENCE",
        indexes = {
                @Index(name = "idx__document_reference__referer_id__referred_title", columnList = "referer_id, referred_title", unique = true),  // 주로 referer 를 기준으로 조회, 제목은 정렬됨.
                @Index(name = "idx__document_reference__referred_title__referer_id", columnList = "referred_title, referer_id")  // 백링크용
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InnerReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "referer_id")
    private Long refererId;
    @Column(name = "referred_title", nullable = false, updatable = false)
    private String referredTitle;

    public InnerReference(Long refererId, String referredTitle) {
        this.refererId = refererId;
        this.referredTitle = referredTitle;
    }
}

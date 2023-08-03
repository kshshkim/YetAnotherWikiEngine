package dev.prvt.yawiki.app.innerreference.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.UUID;

import static dev.prvt.uuid.Const.UUID_V7;

// 복합키 고려할것. clustered index 사용시 읽기 성능에 유리할 것으로 보임. insert 시 성능이 어떨지는 알아봐야함.
@Entity
@Getter
@Table(
        name = "INNER_REFERENCE",
        indexes = {
                @Index(name = "idx__inner_reference__referer_id__referred_title", columnList = "referer_id, referred_title", unique = true),  // 주로 referer 를 기준으로 조회, 제목은 정렬됨.
                @Index(name = "idx__inner_reference__referred_title__referer_id", columnList = "referred_title, referer_id")  // 백링크용
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InnerReference {
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "ref_id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "referer_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)  // 주의! UUID 참조시 BINARY(16) 으로 설정되었는지 확인할것.
    private UUID refererId;
    @Column(name = "referred_title", nullable = false, updatable = false)
    private String referredTitle;

    public InnerReference(UUID refererId, String referredTitle) {
        this.refererId = refererId;
        this.referredTitle = referredTitle;
    }
}

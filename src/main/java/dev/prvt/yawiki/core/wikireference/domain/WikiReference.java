package dev.prvt.yawiki.core.wikireference.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

/**
 * 문서의 참조를 나타내는 엔티티로, 참조의 주체는 문서이고, 참조의 대상은 문서 제목임.
 * 본래 clustered index 환경을 고려하여 대리키를 사용하였으나, 데이터 조회, 삽입 패턴을 고려하여 복합키로 변경함.
 * 많이 참조되는 문서의 경우,
 */
@Entity
@Getter
@Table(
        name = "wiki_reference",
        indexes = {
                @Index(name = "idx__wiki_reference__referred_title__referer_id", columnList = "referred_title, referer_id")  // 백링크용
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WikiReference {

    @EmbeddedId
    private WikiReferenceTuple tuple;

    public String getReferredTitle() {
        return tuple.getReferredTitle();
    }

    public UUID getRefererId() {
        return tuple.getRefererId();
    }

    public WikiReference(UUID refererId, String referredTitle) {
        this.tuple = new WikiReferenceTuple(refererId, referredTitle);
    }
}

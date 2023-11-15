package dev.prvt.yawiki.core.wikireference.domain;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;


@Getter
@Embeddable
@EqualsAndHashCode
public class WikiReferenceTuple implements Serializable {
    @Column(name = "referer_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)  // 주의! UUID 참조시 BINARY(16) 으로 설정되었는지 확인할것.
    private UUID refererId;
    @Column(name = "referred_namespace", columnDefinition = "INTEGER", nullable = false, updatable = false)
    private Namespace namespace;
    @Column(name = "referred_title", nullable = false, updatable = false)
    private String referredTitle;

    protected WikiReferenceTuple() {
    }

    public WikiReferenceTuple(UUID refererId, String referredTitle, Namespace namespace) {
        this.refererId = refererId;
        this.namespace = namespace;
        this.referredTitle = referredTitle;
    }
}

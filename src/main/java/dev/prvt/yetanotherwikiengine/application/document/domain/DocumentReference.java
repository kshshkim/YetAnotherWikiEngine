package dev.prvt.yetanotherwikiengine.application.document.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "DOCUMENT_REFERENCE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referer_id", nullable = false)
    private Document referer;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)  // 참조당하고 있는 문서가 존재하지 않는 경우에도 일단 Document 엔티티는 생성을 해둬야함.
    @JoinColumn(name = "referee_id", nullable = false)
    private Document referee;

    public DocumentReference(Document referer, Document referee) {
        this.referer = referer;
        this.referee = referee;
    }
}

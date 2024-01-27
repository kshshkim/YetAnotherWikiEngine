package dev.prvt.yawiki.core.wikititle.history.domain;

import static java.util.Objects.requireNonNull;

import dev.prvt.yawiki.common.jpa.uuid.UuidV7Generator;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "page_title_log",
        indexes = {
                @Index(
                        name = "idx__page_title_log__created_date",
                        columnList = "created_at"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TitleHistory {
    @Id
    @UuidV7Generator
    @Column(name = "page_title_log_id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "page_title")
    private String pageTitle;
    @Column(name = "namespace")
    private Namespace namespace;
    @Column(name = "title_update_type")
    @Enumerated(EnumType.STRING)
    private TitleUpdateType titleUpdateType;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * DB 저장 x
     */
    @Transient
    private WikiPageTitle wikiPageTitle;

    @Transient
    public WikiPageTitle getWikiPageTitle() {
        if (this.wikiPageTitle == null) {
            this.wikiPageTitle = new WikiPageTitle(getPageTitle(), getNamespace());
        }
        return this.wikiPageTitle;
    }

    @Builder
    protected TitleHistory(
            UUID id,
            String pageTitle,
            Namespace namespace,
            TitleUpdateType titleUpdateType,
            LocalDateTime createdAt,
            WikiPageTitle wikiPageTitle
    ) {
        this.id = id;
        this.pageTitle = pageTitle;
        this.namespace = namespace;
        this.titleUpdateType = titleUpdateType;
        this.createdAt = createdAt;
        this.wikiPageTitle = wikiPageTitle;

        if (wikiPageTitle != null) {
            this.pageTitle = wikiPageTitle.title();
            this.namespace = wikiPageTitle.namespace();
        }

        requireNonNull(this.pageTitle);
        requireNonNull(this.namespace);
        requireNonNull(this.titleUpdateType);
        requireNonNull(this.createdAt);
    }
}

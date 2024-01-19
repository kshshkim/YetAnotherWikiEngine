package dev.prvt.yawiki.core.wikititle.history.domain;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;
import static java.util.Objects.requireNonNull;

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
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
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

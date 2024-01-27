package dev.prvt.yawiki.core.wikipage.domain.model;

import dev.prvt.yawiki.common.util.jpa.uuid.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@Table(name = "raw_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawContent {
    @Id
    @UuidV7Generator
    @Column(name = "raw_id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(updatable = false, columnDefinition = "LONGTEXT")
    private String content;

    public int getSize() {
        return content.length();
    }

    public RawContent(@NotNull String content) {
        if (content == null) {
            throw new NullPointerException("content must not be null");
        }
        this.content = content;
    }
}

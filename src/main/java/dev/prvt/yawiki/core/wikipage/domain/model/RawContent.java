package dev.prvt.yawiki.core.wikipage.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.*;

import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;

@Entity
@Getter
@Table(name = "raw_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawContent {
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
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

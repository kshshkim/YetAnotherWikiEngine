package dev.prvt.yawiki.app.wikipage.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.UUID;

import static dev.prvt.uuid.Const.UUID_V7;

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
    @Column(updatable = false)
    private Integer size;
    @Column(updatable = false, columnDefinition = "LONGTEXT")
    private String content;

    public RawContent(String content) {
        this.content = content;
        this.size = content.length();
    }
}

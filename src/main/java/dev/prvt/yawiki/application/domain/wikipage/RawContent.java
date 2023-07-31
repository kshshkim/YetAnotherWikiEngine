package dev.prvt.yawiki.application.domain.wikipage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "RAW_CONTENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(updatable = false)
    private Integer size;
    @Column(updatable = false)
    private String content;

    public RawContent(String content) {
        this.content = content;
        this.size = content.length();
    }
}

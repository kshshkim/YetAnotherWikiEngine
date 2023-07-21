package dev.prvt.yetanotherwikiengine.application.document.domain;

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
    private Integer size;
    private String content;

    public RawContent(String content) {
        this.content = content;
        this.size = content.length();
    }
}

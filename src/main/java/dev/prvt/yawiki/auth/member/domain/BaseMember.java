package dev.prvt.yawiki.auth.member.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "member")
public abstract class BaseMember {
    @Id
    @Column(name = "member_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "disp_name", unique = true)
    private String displayedName;

    protected BaseMember() {
    }

    public BaseMember(UUID id, String displayedName) {
        this.id = id;
        this.displayedName = displayedName;
    }
}

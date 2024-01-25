package dev.prvt.yawiki.core.contributor.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Getter
@Entity
@DiscriminatorValue("member")
@Table(
        name = "contributor_member",
        indexes = {
                @Index(
                        name = "idx__contributor_member__member_name",
                        columnList = "member_name",
                        unique = true
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberContributor extends Contributor {
    @Column(name = "member_name")
    private String memberName;

    @Override
    public String getName() {
        return memberName;
    }

    @Builder
    protected MemberContributor(UUID id, String memberName) {  // todo memberName validation
        super(id, ContributorState.NORMAL);
        this.memberName = memberName;
    }
}

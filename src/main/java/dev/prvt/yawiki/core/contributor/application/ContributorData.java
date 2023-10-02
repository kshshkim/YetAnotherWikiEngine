package dev.prvt.yawiki.core.contributor.application;

import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.Contributor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ContributorData {
    private final UUID contributorId;
    private final String contributorName;
    private final ContributorType contributorType;

    public static ContributorData from(Contributor contributor) {
        ContributorType type = contributor instanceof AnonymousContributor ? ContributorType.ANON : ContributorType.MEMBER;
        return new ContributorData(contributor.getId(), contributor.getName(), type);
    }
}

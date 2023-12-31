package dev.prvt.yawiki.core.contributor.application;


import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.core.event.MemberJoinEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class ContributorMemberJoinEventHandler {
    private final ContributorRepository contributorRepository;

    @EventListener
    @Transactional
    public void handle(MemberJoinEvent memberJoinEvent) {
        MemberContributor joined = MemberContributor.builder()
                .id(memberJoinEvent.memberId())
                .memberName(memberJoinEvent.displayedName())
                .build();
        contributorRepository.save(joined);
    }
}

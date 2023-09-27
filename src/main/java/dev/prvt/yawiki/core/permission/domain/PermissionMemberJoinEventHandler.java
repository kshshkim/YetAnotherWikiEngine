package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.member.application.MemberJoinEvent;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 회원가입시 자동으로 AuthorityProfile 생성
 */
@Component
@RequiredArgsConstructor
public class PermissionMemberJoinEventHandler {
    private final AuthorityProfileRepository authorityProfileRepository;
    private final DefaultPermissionProperties defaultPermissionProperties;

    public void handle(MemberJoinEvent memberJoinEvent) {
        AuthorityProfile authorityProfile = AuthorityProfile.createWithGroup(memberJoinEvent.memberId(), new PermissionGroup(defaultPermissionProperties.getDefaultPermissionGroupId()), 1);
        authorityProfileRepository.save(authorityProfile);
    }
}

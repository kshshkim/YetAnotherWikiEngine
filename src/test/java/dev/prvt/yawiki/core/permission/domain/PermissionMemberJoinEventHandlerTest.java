package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.event.MemberJoinEvent;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileMemoryRepository;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class PermissionMemberJoinEventHandlerTest {
    UUID givenDefaultGroupId = UUID.randomUUID();
    DefaultPermissionProperties defaultPermissionProperties = new DefaultPermissionProperties(
            true, 0, 0, 0, 0, 3, givenDefaultGroupId
    );
    AuthorityProfileRepository memoryAuthorityProfileRepository = new AuthorityProfileMemoryRepository();
    PermissionMemberJoinEventHandler permissionMemberJoinEventHandler = new PermissionMemberJoinEventHandler(memoryAuthorityProfileRepository, defaultPermissionProperties);

    @Test
    @Transactional
    void handle() {
        // given
        MemberJoinEvent memberJoinEvent = new MemberJoinEvent(UUID.randomUUID(), randString());
        // when
        permissionMemberJoinEventHandler.handle(memberJoinEvent);
        // then
        Optional<AuthorityProfile> found = memoryAuthorityProfileRepository.findById(memberJoinEvent.memberId());
        assertThat(found).isNotEmpty();
        AuthorityProfile authorityProfile = found.get();

        GrantedGroupAuthority grantedGroupAuthority = authorityProfile.getGroupAuthorities().get(0);
        assertThat(grantedGroupAuthority.getAuthorityLevel())
                .describedAs("기본 권한은 1")
                .isEqualTo(1);
        assertThat(grantedGroupAuthority.getGroup().getId())
                .describedAs("property 설정값 따라서 설정돼야함.")
                .isEqualTo(givenDefaultGroupId);
    }


}
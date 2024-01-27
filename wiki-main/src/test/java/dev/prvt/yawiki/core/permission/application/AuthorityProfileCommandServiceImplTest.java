package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.AuthorityGrantValidator;
import dev.prvt.yawiki.core.permission.domain.exception.NoSuchAuthorityProfileException;
import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * 적절히 argument 를 넘기는지에 대한 유닛 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthorityProfileCommandServiceImplTest {

    @Mock
    AuthorityProfileRepository mockAuthorityProfileRepository;

    @Mock
    AuthorityGrantValidator mockAuthorityGrantValidator;

    @InjectMocks
    AuthorityProfileCommandServiceImpl authorityProfileCommandService;

    @Captor
    ArgumentCaptor<AuthorityProfile> authorityProfileArgumentCaptor;
    @Mock
    AuthorityProfile mockGranterProfile;
    @Mock
    AuthorityProfile mockGranteeProfile;

    @Test
    void createAuthorityProfile() {
        // given
        UUID givenActorId = UUID.randomUUID();
        AuthorityProfile expectedAuthorityProfile = AuthorityProfile.create(givenActorId);

        // when
        authorityProfileCommandService.createAuthorityProfile(givenActorId);

        // then
        verify(mockAuthorityProfileRepository)
                .save(authorityProfileArgumentCaptor.capture());
        AuthorityProfile captured = authorityProfileArgumentCaptor.getValue();
        assertThat(captured)
                .describedAs("null 값이 들어가선 안 됨.")
                .isNotNull();
        assertThat(captured.getId())
                .describedAs("ID 적절히 설정됨.")
                .isEqualTo(givenActorId);
        assertThat(captured.getMaxPermissionLevel(0))
                .describedAs("초기 권한 수준이 적절히 설정됨.")
                .isEqualTo(expectedAuthorityProfile.getMaxPermissionLevel(0));
    }

    @Test
    void grantAuthority() {
        // given
        UUID givenGranterId = UUID.randomUUID();
        UUID givenGranteeId = UUID.randomUUID();
        PermissionLevel givenPermissionLevel = PermissionLevel.ASSISTANT_MANAGER;
        String givenComment = randString();
        LocalDateTime givenExp = LocalDateTime.now().plusMinutes(10);

        given(mockAuthorityProfileRepository.findById(givenGranterId))
                .willReturn(Optional.of(mockGranterProfile));

        given(mockAuthorityProfileRepository.findById(givenGranteeId))
                .willReturn(Optional.of(mockGranteeProfile));

        AuthorityGrantData givenGrantData = new AuthorityGrantData(
                givenGranterId,
                givenGranteeId,
                givenPermissionLevel,
                givenExp,
                givenComment
        );

        // when
        authorityProfileCommandService.grantAuthority(givenGrantData);

        // then
        verify(mockGranterProfile)
                .grantPermissionTo(
                        mockGranteeProfile,
                        givenPermissionLevel,
                        givenComment,
                        givenExp,
                        mockAuthorityGrantValidator
                );
    }

    @Test
    void grantPermission_no_such_granter() {
        UUID givenGranterId = UUID.randomUUID();
        UUID givenGranteeId = UUID.randomUUID();

        given(mockAuthorityProfileRepository.findById(givenGranterId))
                .willReturn(Optional.empty());

        AuthorityGrantData givenGrantData = new AuthorityGrantData(
                givenGranterId,
                givenGranteeId,
                PermissionLevel.MEMBER,
                LocalDateTime.now().plusMinutes(10),
                randString()
        );

        // when then
        assertThatThrownBy(() -> authorityProfileCommandService.grantAuthority(givenGrantData))
                .describedAs("granter 존재하지 않는 경우 적절한 예외 반환.")
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(givenGranterId.toString());
    }

    @Test
    void grantPermission_no_such_grantee() {
        UUID givenGranterId = UUID.randomUUID();
        UUID givenGranteeId = UUID.randomUUID();

        given(mockAuthorityProfileRepository.findById(givenGranterId))
                .willReturn(Optional.of(mockGranterProfile));

        given(mockAuthorityProfileRepository.findById(givenGranteeId))
                .willReturn(Optional.empty());

        AuthorityGrantData givenGrantData = new AuthorityGrantData(
                givenGranterId,
                givenGranteeId,
                PermissionLevel.MEMBER,
                LocalDateTime.now().plusMinutes(10),
                randString()
        );

        // when then
        assertThatThrownBy(() -> authorityProfileCommandService.grantAuthority(givenGrantData))
                .describedAs("grantee 존재하지 않는 경우 적절한 예외 반환.")
                .isInstanceOf(NoSuchAuthorityProfileException.class)
                .hasMessageContaining(givenGranteeId.toString());
    }
}
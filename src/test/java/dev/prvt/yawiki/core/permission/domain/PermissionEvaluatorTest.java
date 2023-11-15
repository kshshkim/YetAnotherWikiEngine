package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.exception.PermissionEvaluationException;
import dev.prvt.yawiki.core.permission.domain.model.ActionType;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.model.YawikiPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PermissionEvaluatorTest {
    @Mock
    AuthorityLevelFinder mockAuthorityLevelFinder;

    @Mock
    ResourceAclFinder mockResourceAclFinder;

    @InjectMocks
    PermissionEvaluator permissionEvaluator;

    UUID givenPageId;
    UUID givenActorId;

    @Mock
    YawikiPermission mockYawikiPermission;

    private static Stream<ActionType> actionTypeStream() {
        return Arrays.stream(ActionType.values());
    }

    private static Stream<PermissionLevel> permissionLevelStream() {
        return Arrays.stream(PermissionLevel.values());
    }

    static Stream<Arguments> actionType_requiredPermissionLevel_actorPermissionLevel() {
        return actionTypeStream()
                .flatMap(
                        actionType -> permissionLevelStream().flatMap(
                                required -> permissionLevelStream().map(
                                        actorPermission -> Arguments.arguments(actionType, required, actorPermission)
                                )
                        )
                );
    }

    @BeforeEach
    void init() {
        givenActorId = UUID.randomUUID();
        givenPageId = UUID.randomUUID();
    }

    @ParameterizedTest
    @EnumSource(value = ActionType.class)
    @DisplayName("요구 권한 수준이 EVERYONE인 경우에 대한 테스트")
    void validatePermission_when_pagePermission_is_EVERYONE(ActionType actionType) {
        // given
        given(mockResourceAclFinder.findWikiPageAclByWikiPageId(givenPageId))
                .willReturn(Optional.of(mockYawikiPermission));
        given(mockYawikiPermission.isAllowedToEveryone(actionType))
                .willReturn(true);

        // when then
        assertThatCode(() -> permissionEvaluator.validatePermission(actionType, givenActorId, givenPageId))
                .describedAs("권한 체크에 성공하였기 때문에 예외가 발생하지 않음.")
                .doesNotThrowAnyException();

        verify(mockAuthorityLevelFinder, never().description("EVERYONE인 경우에는 권한 정보를 가져오려 시도하지 않음."))
                .findPermissionLevelByActorId(any());
    }

    @ParameterizedTest
    @MethodSource("actionType_requiredPermissionLevel_actorPermissionLevel")
    @DisplayName("모든 가능한 (행위유형, 필요 권한 수준, 행위자 권한 수준)에 대한 권한 체크 테스트")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void validatePermission_when_pagePermission_is_not_EVERYONE(
            ActionType givenActionType,
            PermissionLevel givenRequiredAuthority,
            PermissionLevel givenActorAuthority
    ) {
        // given
        given(mockResourceAclFinder.findWikiPageAclByWikiPageId(givenPageId))
                .willReturn(Optional.of(mockYawikiPermission));
        given(mockYawikiPermission.isAllowedToEveryone(givenActionType))
                .willReturn(givenRequiredAuthority.equals(PermissionLevel.EVERYONE));
        given(mockYawikiPermission.canDo(givenActionType, givenActorAuthority))
                .willAnswer(invocation -> ((PermissionLevel) invocation.getArgument(1)).isHigherThanOrEqualTo(givenRequiredAuthority));
        given(mockAuthorityLevelFinder.findPermissionLevelByActorId(givenActorId))
                .willReturn(givenActorAuthority);

        // when then
        if (givenActorAuthority.isHigherThanOrEqualTo(givenRequiredAuthority)) {
            assertThatCode(() -> permissionEvaluator.validatePermission(givenActionType, givenActorId, givenPageId))
                    .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> permissionEvaluator.validatePermission(givenActionType, givenActorId, givenPageId))
                    .isInstanceOf(PermissionEvaluationException.class);
        }
    }
}
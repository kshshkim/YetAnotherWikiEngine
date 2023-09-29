package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.evaluator.PermissionEvaluator;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WikiPageCommandPermissionValidatorImplTest {

    UUID givenActorId;
    UUID givenResourceId;
    WikiPage givenWikiPage;

    ActionType calledActionType;
    UUID calledActorId;
    UUID calledResourceId;

    PermissionEvaluator permissionComparator = new PermissionEvaluator() {
        @Override
        public void validatePermission(UUID actorId, UUID resourceId, ActionType actionType) {
            calledActionType = actionType;
            calledActorId = actorId;
            calledResourceId = resourceId;
        }
    };

    WikiPageCommandPermissionValidatorImpl wikiPageCommandPermissionValidator = new WikiPageCommandPermissionValidatorImpl(permissionComparator);

    @BeforeEach
    @SneakyThrows
    void init() {
        calledActionType = null;
        calledActorId = null;
        calledResourceId = null;

        givenActorId = UUID.randomUUID();
        givenWikiPage = WikiPage.create(UUID.randomUUID().toString());
        givenResourceId = UUID.randomUUID();

        Class<? extends WikiPage> aClass = givenWikiPage.getClass();
        Field id = aClass.getDeclaredField("id");
        id.setAccessible(true);
        id.set(givenWikiPage, givenResourceId);  // 리플렉션으로 ID 설정


    }

    @Test
    void validateUpdate() {
        // when
        wikiPageCommandPermissionValidator.validateUpdate(givenActorId, givenWikiPage);

        // then
        assertThat(calledActionType)
                .isNotNull()
                .isEqualTo(ActionType.UPDATE);
        assertThat(calledActorId)
                .isNotNull()
                .isEqualTo(givenActorId);
        assertThat(calledResourceId)
                .isNotNull()
                .isEqualTo(givenResourceId);
    }

    @Test
    void validateDelete() {
        // when
        wikiPageCommandPermissionValidator.validateDelete(givenActorId, givenWikiPage);

        // then
        assertThat(calledActionType)
                .isNotNull()
                .isEqualTo(ActionType.DELETE);
        assertThat(calledActorId)
                .isNotNull()
                .isEqualTo(givenActorId);
        assertThat(calledResourceId)
                .isNotNull()
                .isEqualTo(givenResourceId);

    }

    @Test
    void validateUpdateProclaim() {
        // when
        wikiPageCommandPermissionValidator.validateUpdate(givenActorId, givenWikiPage);

        // then
        assertThat(calledActionType)
                .isNotNull()
                .isEqualTo(ActionType.UPDATE);
        assertThat(calledActorId)
                .isNotNull()
                .isEqualTo(givenActorId);
        assertThat(calledResourceId)
                .isNotNull()
                .isEqualTo(givenResourceId);
    }
}
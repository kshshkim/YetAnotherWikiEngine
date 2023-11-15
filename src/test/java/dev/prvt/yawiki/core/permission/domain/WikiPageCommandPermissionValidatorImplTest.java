package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.ActionType;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.mockito.Mockito.verify;

/**
 * 비즈니스 로직이 들어가지 않는 단순 어댑터 역할만 하는 클래스
 * 인자를 잘 넘기는지만 테스트함.
 */
@ExtendWith(MockitoExtension.class)
class WikiPageCommandPermissionValidatorImplTest {

    @Mock
    PermissionEvaluator mockPermissionEvaluator;

    @InjectMocks
    WikiPageCommandPermissionValidatorImpl wikiPageCommandPermissionValidator;


    UUID givenContributorId;
    UUID givenWikiPageId;
    WikiPage givenWikiPage;

    @SneakyThrows
    @BeforeEach
    void init() {
        givenContributorId = UUID.randomUUID();
        givenWikiPageId = UUID.randomUUID();
        givenWikiPage = WikiPage.create(randString(), Namespace.NORMAL);
        Field wikiPageIdField = WikiPage.class.getDeclaredField("id");
        wikiPageIdField.setAccessible(true);
        wikiPageIdField.set(givenWikiPage, givenWikiPageId);  // 리플렉션으로 ID 지정
    }

    @Test
    void validateEditCommit() {
        // when
        wikiPageCommandPermissionValidator.validateEditCommit(givenContributorId, givenWikiPage);

        // then
        verify(mockPermissionEvaluator).validatePermission(ActionType.EDIT_COMMIT, givenContributorId, givenWikiPageId);
    }

    @Test
    void validateRename() {
        // when
        wikiPageCommandPermissionValidator.validateRename(givenContributorId, givenWikiPage);

        // then
        verify(mockPermissionEvaluator).validatePermission(ActionType.RENAME, givenContributorId, givenWikiPageId);
    }

    @Test
    void validateCreate() {
        // when
        wikiPageCommandPermissionValidator.validateCreate(givenContributorId, givenWikiPage);

        // then
        verify(mockPermissionEvaluator).validatePermission(ActionType.CREATE, givenContributorId, givenWikiPageId);
    }

    @Test
    void validateDelete() {
        // when
        wikiPageCommandPermissionValidator.validateDelete(givenContributorId, givenWikiPage);

        // then
        verify(mockPermissionEvaluator).validatePermission(ActionType.DELETE, givenContributorId, givenWikiPageId);
    }

    @Test
    void validateEditRequest() {
        // when
        wikiPageCommandPermissionValidator.validateEditRequest(givenContributorId, givenWikiPage);

        // then
        verify(mockPermissionEvaluator).validatePermission(ActionType.EDIT_REQUEST, givenContributorId, givenWikiPageId);
    }
}
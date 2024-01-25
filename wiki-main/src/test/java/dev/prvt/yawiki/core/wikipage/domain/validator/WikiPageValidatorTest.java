package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aNormalWikiPage;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WikiPageValidatorTest {

    @Mock
    VersionCollisionValidator versionCollisionValidator;
    @Mock
    WikiPageCommandPermissionValidator commandPermissionValidator;
    @Mock
    WikiPageDuplicateTitleValidator duplicateTitleValidator;

    @InjectMocks
    WikiPageValidator wikiPageValidator;

    UUID givenContributorId;
    String givenVersionToken;
    WikiPage givenWikiPage;

    @BeforeEach
    void init() {
        givenContributorId = UUID.randomUUID();
        givenVersionToken = UUID.randomUUID().toString();
        givenWikiPage = aNormalWikiPage();
    }

    @Test
    void validateDelete() {
        // when
        wikiPageValidator.validateDelete(givenContributorId, givenVersionToken, givenWikiPage);

        // then
        verify(versionCollisionValidator).validate(givenWikiPage, givenVersionToken);
        verify(commandPermissionValidator).validateDelete(givenContributorId, givenWikiPage);
    }

    @Test
    void validateProclaim() {
        // when
        wikiPageValidator.validateProclaim(givenContributorId, givenWikiPage);

        // then
        verify(commandPermissionValidator).validateEditRequest(givenContributorId, givenWikiPage);
    }

    @Test
    void validateRename() {
        String newTitle = UUID.randomUUID().toString();

        // when
        wikiPageValidator.validateRename(givenContributorId, givenVersionToken, givenWikiPage, newTitle);

        // then
        verify(versionCollisionValidator).validate(givenWikiPage, givenVersionToken);
        verify(duplicateTitleValidator).validate(eq(new WikiPageTitle(newTitle, givenWikiPage.getNamespace())));
        verify(commandPermissionValidator).validateRename(givenContributorId, givenWikiPage);
    }

    @Test
    void validateUpdate() {
        // when
        wikiPageValidator.validateUpdate(givenContributorId, givenVersionToken, givenWikiPage);

        // then
        verify(commandPermissionValidator).validateEditCommit(givenContributorId, givenWikiPage);
    }

    @Test
    void validateCreate() {
        // when
        wikiPageValidator.validateCreate(givenContributorId, givenWikiPage.getWikiPageTitle());

        // then
        verify(duplicateTitleValidator).validate(eq(givenWikiPage.getWikiPageTitle()));
    }
}
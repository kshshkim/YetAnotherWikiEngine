package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WikiPageCreatedEventHandlerTest {

    @Mock
    ResourcePermissionService mockResourcePermissionService;

    @InjectMocks
    WikiPageCreatedEventHandler wikiPageCreatedEventHandler;


    @Test
    void handle() {
        // given
        WikiPageTitle createdWikiPageTitle = new WikiPageTitle(UUID.randomUUID().toString(), Namespace.NORMAL);
        UUID createdWikiPageId = UUID.randomUUID();
        WikiPageCreatedEvent givenEvent = new WikiPageCreatedEvent(
                createdWikiPageId,
                createdWikiPageTitle,
                LocalDateTime.now()
        );

        // when
        wikiPageCreatedEventHandler.handle(givenEvent);

        // then
        verify(mockResourcePermissionService)
                .createPagePermission(createdWikiPageId, createdWikiPageTitle.namespace().getIntValue());
    }
}
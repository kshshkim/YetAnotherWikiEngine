package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageCreatedEventHandler {
    private final ResourcePermissionService resourcePermissionService;

    @EventListener
    public void handle(WikiPageCreatedEvent wikiPageCreatedEvent) {
        resourcePermissionService.updateResourcePermission(wikiPageCreatedEvent.id());
    }
}

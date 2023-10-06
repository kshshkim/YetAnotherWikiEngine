package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.PermissionData;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WikiPageCreatedEventHandlerTest {
    // todo 유닛테스트
    static UUID updatedWikiPageId;

    static class DummyResourcePermissionService implements ResourcePermissionService {
        @Override
        public void updateResourcePermission(UUID resourceId) {
            updatedWikiPageId = resourceId;
        }

        @Override
        public void updateResourcePermission(UUID resourceId, UUID permissionGroupId) {
        }

        @Override
        public void updateResourcePermission(UUID resourceId, UUID permissionGroupId, PermissionData permissionData) {
        }
    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        public ResourcePermissionService resourcePermissionService() {
            return new DummyResourcePermissionService();
        }
        @Bean
        public WikiPageCreatedEventHandler wikiPageCreatedEventHandler() {
            return new WikiPageCreatedEventHandler(resourcePermissionService());
        }
    }


    @Autowired
    ApplicationEventPublisher applicationEventPublisher;


    @BeforeEach
    void init() {
        updatedWikiPageId = null;
    }

    @Test
    void should_call_resource_permission_service_with_proper_arguments_and_methods() {
        WikiPageCreatedEvent wikiPageCreatedEvent = new WikiPageCreatedEvent(UUID.randomUUID(), randString());

        // when
        applicationEventPublisher.publishEvent(wikiPageCreatedEvent);

        // then
        assertThat(updatedWikiPageId)
                .isNotNull()
                .isEqualTo(wikiPageCreatedEvent.id());
    }
}
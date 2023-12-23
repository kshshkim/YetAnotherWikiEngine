package dev.prvt.yawiki.core.wikireference.application;

import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageDeletedEvent;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageUpdateCommittedEvent;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceUpdater;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.mockito.Mockito.verify;

@Configuration
class TestConf {
    @Autowired
    WikiReferenceUpdater wikiReferenceUpdater;

    @Bean
    public WikiPageUpdatedEventListener wikiPageUpdatedEventListener() {
        return new WikiPageUpdatedEventListener(wikiReferenceUpdater);
    }
}

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestConf.class)
class WikiPageUpdatedEventListenerTest {

    @MockBean
    WikiReferenceUpdater wikiReferenceUpdater;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;


    @Test
    void handle_updateCommitted() {
        Set<WikiPageTitle> referencedTitles = Stream.generate(WikiPageFixture::aWikiPageTitle)
                .limit(20)
                .collect(Collectors.toSet());
        WikiPageUpdateCommittedEvent givenEvent = new WikiPageUpdateCommittedEvent(UUID.randomUUID(), UUID.randomUUID(), aWikiPageTitle(), referencedTitles);

        // when
        applicationEventPublisher.publishEvent(givenEvent);

        // then
        verify(wikiReferenceUpdater).updateReferences(givenEvent.wikiPageId(), givenEvent.referencedTitles());
    }

    @Test
    void handle_deactivated() {
        WikiPageDeletedEvent givenEvent = new WikiPageDeletedEvent(UUID.randomUUID(), UUID.randomUUID(), aWikiPageTitle(), LocalDateTime.now());

        // when
        applicationEventPublisher.publishEvent(givenEvent);

        // then
        verify(wikiReferenceUpdater).deleteReferences(givenEvent.wikiPageId());
    }
}
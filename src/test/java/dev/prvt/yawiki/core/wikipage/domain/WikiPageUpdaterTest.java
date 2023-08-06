package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WikiPageUpdaterTest {
    private final String EXCEPTION_TRIGGER = randString();
    private final String EXCEPTION_TRIGGERED_MESSAGE = randString();
    private boolean wikiReferenceUpdaterCalled;
    private WikiReferenceUpdater wikiReferenceUpdater = new WikiReferenceUpdater() {
        @Override
        public void updateReferences(UUID documentId, Set<String> referencedTitles) {
            wikiReferenceUpdaterCalled = true;
            if (referencedTitles.contains(EXCEPTION_TRIGGER)) {
                throw new RuntimeException(EXCEPTION_TRIGGERED_MESSAGE);
            }
        }
    };

    @BeforeEach
    void init() {
        wikiReferenceUpdaterCalled = false;
    }

    private WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private WikiPageUpdater wikiPageUpdater = new WikiPageUpdater(wikiPageRepository, wikiReferenceUpdater);

    @Test
    void update_should_fail_when_wiki_page_does_not_exist() {
        Runnable should_fail = () -> wikiPageUpdater.update(UUID.randomUUID(), randString(), randString(), randString(), Set.of("ref1"));
        assertThatThrownBy(should_fail::run)
                .isInstanceOf(WikiPageUpdaterException.class)
                .hasMessageContaining("no such ")
        ;
    }
    @Test
    void should_throw_ReferenceUpdateException_when_update_failed() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));

        assertThatThrownBy(() -> wikiPageUpdater.update(UUID.randomUUID(), saved.getTitle(), randString(), randString(), Set.of(EXCEPTION_TRIGGER)))
                .isInstanceOf(WikiPageUpdaterException.class)
                .hasMessageContaining(EXCEPTION_TRIGGERED_MESSAGE)
        ;
    }

    @Test
    void should_call_wikiReferenceUpdater() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        wikiPageUpdater.update(UUID.randomUUID(), saved.getTitle(), randString(), randString(), Set.of());

        assertThat(wikiReferenceUpdaterCalled)
                .isTrue();
    }
}
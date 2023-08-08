package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageUpdaterException;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageValidator;
import dev.prvt.yawiki.core.wikipage.domain.wikireference.WikiReferenceUpdater;
import dev.prvt.yawiki.core.wikipage.infra.repository.WikiPageMemoryRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.*;

class WikiPageDomainServiceTest {
    private final String EXCEPTION_TRIGGER = randString();
    private final String EXCEPTION_TRIGGERED_MESSAGE = randString();

    private final String VALIDATOR_UPDATE_EXCEPTION_TRIGGER = UUID.randomUUID().toString();
    private final String VALIDATOR_UPDATE_EXCEPTION_MESSAGE = UUID.randomUUID().toString();

    private final UUID VALIDATOR_PROCLAIM_EXCEPTION_TRIGGER = UUID.randomUUID();
    private final String VALIDATOR_PROCLAIM_EXCEPTION_MESSAGE = UUID.randomUUID().toString();

    private boolean wikiReferenceUpdaterCalled;
    private boolean wikiPageValidatorUpdateCommitCalled;
    private boolean wikiPageValidatorProclaimCalled;

    private final WikiReferenceUpdater wikiReferenceUpdater = new WikiReferenceUpdater() {
        @Override
        public void updateReferences(UUID documentId, Set<String> referencedTitles) {
            wikiReferenceUpdaterCalled = true;
            if (referencedTitles.contains(EXCEPTION_TRIGGER)) {
                throw new RuntimeException(EXCEPTION_TRIGGERED_MESSAGE);
            }
        }
    };

    class DummyWikiPageValidator extends WikiPageValidator {
        public DummyWikiPageValidator() {
            super(null, null);
        }

        @SneakyThrows
        @Override
        public void validateUpdateCommit(UUID actorId, String versionToken, WikiPage wikiPage) {
//            super.validate(actorId, wikiPageTitle, versionToken);
            wikiPageValidatorUpdateCommitCalled = true;
            if (versionToken.equals(VALIDATOR_UPDATE_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(VALIDATOR_UPDATE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {
            wikiPageValidatorProclaimCalled = true;
            if (actorId.equals(VALIDATOR_PROCLAIM_EXCEPTION_TRIGGER)) {
                throw new RuntimeException(VALIDATOR_PROCLAIM_EXCEPTION_MESSAGE);
            }
        }
    }


    @BeforeEach
    void init() {
        wikiReferenceUpdaterCalled = false;
        wikiPageValidatorUpdateCommitCalled = false;
    }

    private final WikiPageRepository wikiPageRepository = new WikiPageMemoryRepository();

    private final WikiPageDomainService wikiPageDomainService = new WikiPageDomainService(wikiPageRepository, wikiReferenceUpdater, new DummyWikiPageValidator());

    @Test
    void update_should_fail_when_wiki_page_does_not_exist() {
        Runnable should_fail = () -> wikiPageDomainService.update(UUID.randomUUID(), randString(), randString(), randString(), randString(), Set.of("ref1"));
        assertThatThrownBy(should_fail::run)
                .isInstanceOf(NoSuchWikiPageException.class)
        ;
    }
    @Test
    void should_throw_ReferenceUpdateException_when_update_failed() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));

        assertThatThrownBy(() -> wikiPageDomainService.update(UUID.randomUUID(), saved.getTitle(), randString(), randString(), randString(), Set.of(EXCEPTION_TRIGGER)))
                .isInstanceOf(WikiPageUpdaterException.class)
                .hasMessageContaining(EXCEPTION_TRIGGERED_MESSAGE)
        ;
    }

    @Test
    void should_call_wikiReferenceUpdater() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        wikiPageDomainService.update(UUID.randomUUID(), saved.getTitle(), randString(), randString(), randString(), Set.of());

        assertThat(wikiReferenceUpdaterCalled)
                .isTrue();
    }

    @Test
    void should_call_wiki_page_validator() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        wikiPageDomainService.update(UUID.randomUUID(), saved.getTitle(), randString(), randString(), randString(), Set.of());

        assertThat(wikiPageValidatorUpdateCommitCalled).isTrue();
    }

    @Test
    void should_fail_update_if_validator_fails() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        assertThatThrownBy(() ->
                wikiPageDomainService.update(UUID.randomUUID(), saved.getTitle(), randString(), randString(), VALIDATOR_UPDATE_EXCEPTION_TRIGGER, Set.of()))
                .hasMessageContaining(VALIDATOR_UPDATE_EXCEPTION_MESSAGE)
        ;
    }

    @Test
    void should_update_with_proper_parameters() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));

        UUID contributorId = UUID.randomUUID();
        String title = saved.getTitle();
        String content = randString();
        String comment = randString();
        String versionToken = UUID.randomUUID().toString();

        wikiPageDomainService.update(contributorId, title, content, comment, versionToken, Set.of());

        Revision updatedRev = saved.getCurrentRevision();

        // then
        assertThat(wikiReferenceUpdaterCalled)
                .isTrue();
        assertThat(saved.getTitle())
                .isEqualTo(title);
        assertThat(updatedRev)
                .isNotNull();
        assertThat(updatedRev.getComment())
                .isEqualTo(comment);
        assertThat(updatedRev.getRawContent())
                .isNotNull();
        assertThat(updatedRev.getRawContent().getContent())
                .isEqualTo(content);
    }

    @Test
    void should_call_validator_when_proclaim() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        wikiPageDomainService.proclaimUpdate(UUID.randomUUID(), saved.getTitle());

        assertThat(wikiPageValidatorProclaimCalled).isTrue();
    }

    @Test
    void proclaimUpdate_should_create_when_does_not_exist() {
        String notExists = UUID.randomUUID().toString();

        WikiPage wikiPage = wikiPageDomainService.proclaimUpdate(UUID.randomUUID(), notExists);

        assertThat(wikiPage)
                .describedAs("WikiPage 가 생성되어야함.")
                .isNotNull();

        assertThat(wikiPageRepository.findByTitle(notExists))
                .describedAs("생성된 WikiPage 가 영속화 되어야함.")
                .isPresent();

        assertThat(wikiPage.getTitle())
                .describedAs("생성된 WikiPage 의 문서 제목이 동일함.")
                .isEqualTo(notExists);

        assertThat(wikiPage.isActive())
                .describedAs("새로 생성된 WikiPage 엔티티의 isActive 는 false 임")
                .isFalse();
    }

    @Test
    void proclaimUpdate_should_get_when_exists() {
        WikiPage saved = wikiPageRepository.save(WikiPage.create(randString()));
        saved.update(UUID.randomUUID(), randString(), randString());
        String savedContent = saved.getContent();

        // when
        WikiPage when = wikiPageDomainService.proclaimUpdate(UUID.randomUUID(), saved.getTitle());

        // then
        assertThat(when.getContent())
                .isEqualTo(savedContent);

    }
}
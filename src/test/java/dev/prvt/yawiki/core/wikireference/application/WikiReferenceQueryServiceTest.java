package dev.prvt.yawiki.core.wikireference.application;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WikiReferenceQueryServiceTest {

    @Mock
    WikiReferenceRepository wikiReferenceRepository;

    @InjectMocks
    WikiReferenceQueryService wikiReferenceQueryService;


    @Test
    void getBackReferences() {
        // given
        WikiPageTitle givenWikiPageTitle = aWikiPageTitle();
        Pageable givenPageable = Pageable.ofSize(10).withPage(20);

        // when
        wikiReferenceQueryService.getBackReferences(givenWikiPageTitle, givenPageable);

        // then
        verify(wikiReferenceRepository)
                .findBackReferencesByWikiPageTitle(givenWikiPageTitle.title(), givenWikiPageTitle.namespace(), givenPageable);
    }
}
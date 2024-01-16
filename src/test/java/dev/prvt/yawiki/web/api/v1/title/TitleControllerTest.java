package dev.prvt.yawiki.web.api.v1.title;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.web.converter.NamespaceParser;
import dev.prvt.yawiki.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.web.converter.WikiPageTitleConverterImpl;
import dev.prvt.yawiki.core.wikititle.existence.WikiPageTitleExistenceChecker;
import dev.prvt.yawiki.web.api.v1.title.response.TitleListResponse;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebMvcTest(TitleController.class)
class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WikiPageTitleConverter wikiPageTitleConverter;

    @MockBean
    private ContributorInfoArgumentResolver contributorInfoArgumentResolver;

    @Autowired
    private GenericConversionService genericConversionService;

    @MockBean
    private WikiPageTitleExistenceChecker wikiPageTitleExistenceChecker;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void init() {
        genericConversionService.addConverter(new WikiPageTitleConverterImpl(new NamespaceParser(new HashMap<>())));
    }

    @Captor
    ArgumentCaptor<Collection<WikiPageTitle>> wikiPageTitlesCaptor;

    @Test
    @WithMockUser
    @SneakyThrows
    void getNonExistTitles() {
        int titleCount = 250;

        List<WikiPageTitle> nonExistentTitles = Stream.generate(() -> aWikiPageTitle())
                .limit(titleCount)
                .toList();

        given(wikiPageTitleExistenceChecker.filterExistentTitles(anyCollection()))
                .willReturn(nonExistentTitles);


        MockHttpServletRequestBuilder uriBuilder = MockMvcRequestBuilders.get("/api/v1/title/filter/nonexistent");
        for (WikiPageTitle nonExistentTitle : nonExistentTitles) {
            uriBuilder.param("t", nonExistentTitle.toUnparsedString());
        }

        // when
        MvcResult result = mockMvc.perform(uriBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        TitleListResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), TitleListResponse.class);

        // then
        verify(wikiPageTitleExistenceChecker).filterExistentTitles(wikiPageTitlesCaptor.capture());
        Collection<WikiPageTitle> captured = wikiPageTitlesCaptor.getValue();

        // assert captured
        assertThat(captured)
                .describedAs("url 파라미터가 적절하게 변환되어 내부로 넘겨짐.")
                .containsExactlyInAnyOrderElementsOf(nonExistentTitles);

        // assert response
        assertThat(response.titles())
                .describedAs("응답 값이 제대로 변환되어야함.")
                .containsExactlyInAnyOrderElementsOf(nonExistentTitles);

    }
}
package dev.prvt.yawiki.titleexistence.cache.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.testutil.CommonFixture;
import dev.prvt.yawiki.common.webutil.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.titleexistence.cache.application.WikiPageTitleExistenceFilter;
import dev.prvt.yawiki.titleexistence.web.api.TitleController;
import dev.prvt.yawiki.titleexistence.web.api.response.TitleListResponse;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(TitleController.class)
class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WikiPageTitleConverter wikiPageTitleConverter;

    @MockBean
    private WikiPageTitleExistenceFilter wikiPageTitleExistenceChecker;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void init() {
    }

    @Captor
    ArgumentCaptor<Collection<WikiPageTitle>> wikiPageTitlesCaptor;

    @Test
    @SneakyThrows
    void getNonExistTitles() {
        int titleCount = 250;

        List<WikiPageTitle> nonExistentTitles = Stream.generate(CommonFixture::aWikiPageTitle)
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
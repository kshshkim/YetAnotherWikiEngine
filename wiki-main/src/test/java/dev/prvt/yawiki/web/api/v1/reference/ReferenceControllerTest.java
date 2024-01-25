package dev.prvt.yawiki.web.api.v1.reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikireference.application.WikiReferenceQueryService;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import dev.prvt.yawiki.web.api.v1.reference.response.BacklinkResponse;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import dev.prvt.yawiki.common.util.NamespaceParser;
import dev.prvt.yawiki.common.webutil.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.common.webutil.converter.WikiPageTitleConverterImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.WikiPageFixture.aWikiPageTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ReferenceController.class)
class ReferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WikiPageTitleConverter wikiPageTitleConverter;

    @MockBean
    private ContributorInfoArgumentResolver contributorInfoArgumentResolver;

    @Autowired
    private GenericConversionService genericConversionService;

    @MockBean
    private WikiReferenceQueryService wikiReferenceQueryService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void init() {
        genericConversionService.addConverter(new WikiPageTitleConverterImpl(new NamespaceParser(new HashMap<>())));
    }


    @SneakyThrows
    @WithMockUser
    @Test
    void getBackLinks() {
        WikiPageTitle givenTitle = aWikiPageTitle();
        List<WikiPageTitle> givenReferrers = Stream.generate(WikiPageFixture::aWikiPageTitle)
                .limit(50)
                .toList();

        given(wikiReferenceQueryService.getBacklinks(eq(givenTitle), any()))
                .willAnswer(a -> {
                    Pageable pageableArg = a.getArgument(1, Pageable.class);
                    return new PageImpl<WikiPageTitle>(givenReferrers, pageableArg, givenReferrers.size());
                });

        int givenPageSize = 501;
        int givenPageNumber = 11;


        // when
        MvcResult result = mockMvc.perform(
                    get("/api/v1/reference/backlink/" + givenTitle.toUnparsedString())
                            .param("size", Integer.toString(givenPageSize))
                            .param("page", Integer.toString(givenPageNumber))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        BacklinkResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BacklinkResponse.class);

        // then
        assertThat(response.referredTitle())
                .describedAs("referredTitle 값 검증")
                .isEqualTo(givenTitle);
        assertThat(response.referrers())
                .describedAs("결과값 검증")
                .containsExactlyElementsOf(givenReferrers);
    }
}
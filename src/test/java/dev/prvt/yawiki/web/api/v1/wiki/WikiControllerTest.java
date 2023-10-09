package dev.prvt.yawiki.web.api.v1.wiki;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import dev.prvt.yawiki.core.permission.domain.PermissionEvaluationException;
import dev.prvt.yawiki.core.wikipage.application.WikiPageCommandService;
import dev.prvt.yawiki.core.wikipage.application.WikiPageQueryService;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.web.api.v1.ErrorMessage;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArg;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import dev.prvt.yawiki.web.contributorresolver.converters.AnonymousAuthenticationTokenContributorInfoConverter;
import dev.prvt.yawiki.web.contributorresolver.converters.JwtAuthenticationTokenContributorInfoConverter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.config.Customizer.withDefaults;

@WebMvcTest(WikiController.class)
class WikiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private WikiPageCommandService wikiPageCommandService;

    @MockBean
    private WikiPageQueryService wikiPageQueryService;

    @MockBean
    private AnonymousAuthenticationTokenContributorInfoConverter anonymousAuthenticationTokenContributorInfoConverter;

    @MockBean
    private JwtAuthenticationTokenContributorInfoConverter jwtAuthenticationTokenContributorInfoConverter;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // security
    @TestConfiguration
    static class SecurityConf {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .authorizeHttpRequests(authorizeHttpRequestsCustomizer())
                    .sessionManagement(sessionManagementCustomizer())
                    .csrf(csrfCustomizer())
                    .cors(conf -> conf.configure(httpSecurity))
                    .oauth2ResourceServer(auth2ResourceServerCustomizer())
            ;

            return httpSecurity.build();
        }

        @Bean
        public CorsConfiguration corsConfiguration() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedOrigin("*");

            return corsConfiguration;
        }

        @NotNull
        private static Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> auth2ResourceServerCustomizer() {
            return configurer -> configurer.jwt(withDefaults());
        }

        @NotNull
        private static Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer() {
            return AbstractHttpConfigurer::disable;
        }

        @NotNull
        private static Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementCustomizer() {
            return session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        @NotNull
        private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer() {
            return auth -> {
                auth.anyRequest().permitAll();
            };
        }
        @Bean
        public KeyPair keyPair() throws NoSuchAlgorithmException {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }

        @Bean
        public RSAKey rsaKey(KeyPair keyPair) {
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey(keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
        }

        @Bean
        public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
            JWKSet jwkSet = new JWKSet(rsaKey);
            return (((jwkSelector, context) -> jwkSelector.select(jwkSet)));
        }

        @Bean
        public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
            return NimbusJwtDecoder
                    .withPublicKey(rsaKey.toRSAPublicKey())
                    .build();
        }

        @Bean
        public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
            return new NimbusJwtEncoder(jwkSource);
        }

    }
    // end security

    String givenTitle;
    String givenContent;
    UUID givenContributorId;
    String givenVersionToken;
    String givenMessage;


    @MockBean
    ContributorInfoArgumentResolver contributorInfoArgumentResolver;

    @SneakyThrows
    @BeforeEach
    void init() {
        givenTitle = randString();
        givenContent = randString() + randString() + randString();
        givenContributorId = UUID.randomUUID();
        givenVersionToken = UUID.randomUUID().toString();
        givenMessage = randString();
        given(contributorInfoArgumentResolver.supportsParameter(any())).willReturn(true);
        given(contributorInfoArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new ContributorInfoArg(givenContributorId));
    }

    @SneakyThrows
    @WithAnonymousUser
    @Test
    void getWikiPage_not_found() {
        // given
        given(wikiPageQueryService.getWikiPage("notexist"))
                .willThrow(new NoSuchWikiPageException());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wiki/notexist"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorMessage errorMessage = objectMapper.readValue(contentAsString, ErrorMessage.class);
        assertThat(tuple(errorMessage.getStatus(), errorMessage.getMessage(), errorMessage.getPath()))
                .isEqualTo(tuple(HttpStatus.NOT_FOUND.value(), null, "/api/v1/wiki/notexist"));
    }

    @SneakyThrows
    @WithAnonymousUser
    @Test
    void getWikiPage_ok() {
        // given
        given(wikiPageQueryService.getWikiPage(givenTitle))
                .willReturn(new WikiPageDataForRead(givenTitle, givenContent, new ArrayList<>()));

        // when then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wiki/" + givenTitle))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        WikiPageDataForRead response = objectMapper.readValue(contentAsString, WikiPageDataForRead.class);

        assertThat(tuple(response.title(), response.content()))
                .isEqualTo(tuple(givenTitle, givenContent));
    }

    @SneakyThrows
    @Test
    void getWikiHistory_not_found() {
        given(wikiPageQueryService.getRevisionHistory(eq(givenTitle), any()))
                .willThrow(new NoSuchWikiPageException());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wiki/" + givenTitle + "/history"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorMessage errorMessage = objectMapper.readValue(contentAsString, ErrorMessage.class);
        assertThat(tuple(errorMessage.getStatus(), errorMessage.getMessage(), errorMessage.getPath()))
                .isEqualTo(tuple(HttpStatus.NOT_FOUND.value(), null, "/api/v1/wiki/" + givenTitle + "/history"));
    }

    @SneakyThrows
    @Test
    void getWikiHistory_ok_default_() {
        Random r = new Random();
        List<RevisionData> revs = IntStream.range(1, 10)
                .mapToObj(i -> new RevisionData(i, r.nextInt(10), randString(), randString()))
                .toList();
        Pageable givenPageable = Pageable.ofSize(30).withPage(0);
        given(wikiPageQueryService.getRevisionHistory(eq(givenTitle), any()))
                .willReturn(new PageImpl<>(revs, givenPageable, revs.size()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wiki/" + givenTitle + "/history"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(new PageImpl<>(revs, givenPageable, revs.size())));
    }

    @SneakyThrows
    @Test
    @WithAnonymousUser
    void proclaimEdit() {
        given(wikiPageCommandService.proclaimUpdate(givenContributorId, givenTitle))
                .willReturn(new WikiPageDataForUpdate(givenTitle, givenContent, givenVersionToken));

        // when then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wiki/" + givenTitle + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        WikiPageDataForUpdate wikiPageDataForUpdate = objectMapper.readValue(contentAsString, WikiPageDataForUpdate.class);
        assertThat(tuple(wikiPageDataForUpdate.title(), wikiPageDataForUpdate.content(), wikiPageDataForUpdate.versionToken()))
                .isEqualTo(tuple(givenTitle, givenContent, givenVersionToken));
    }

    @SneakyThrows
    @Test
    @WithAnonymousUser
    void proclaimEdit_permission_evaluation_exception() {
        givenMessage = randString();
        given(wikiPageCommandService.proclaimUpdate(givenContributorId, givenTitle))
                .willThrow(new PermissionEvaluationException(givenMessage));

        // when then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wiki/" + givenTitle + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorMessage errorMessage = objectMapper.readValue(contentAsString, ErrorMessage.class);
        assertThat(tuple(errorMessage.getStatus(), errorMessage.getMessage(), errorMessage.getPath()))
                .isEqualTo(tuple(HttpStatus.FORBIDDEN.value(), givenMessage, "/api/v1/wiki/" + givenTitle + "/edit"));
    }

    @SneakyThrows
    @Test
    @WithAnonymousUser
    void commitEdit_version_collision_exception() {
        String givenComment = randString();
        givenMessage = randString();
        doThrow(new VersionCollisionException(givenMessage)).when(wikiPageCommandService).commitUpdate(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent);
        String body = objectMapper.writeValueAsString(new WikiController.CommitEditRequest(givenComment, givenVersionToken, givenContent));

        // when
        MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/wiki/" + givenTitle + "/edit")
                            .content(body)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorMessage errorMessage = objectMapper.readValue(contentAsString, ErrorMessage.class);
        assertThat(tuple(errorMessage.getStatus(), errorMessage.getMessage(), errorMessage.getPath()))
                .isEqualTo(tuple(HttpStatus.CONFLICT.value(), "versionToken mismatch: " + givenMessage, "/api/v1/wiki/" + givenTitle + "/edit"));
    }
    @SneakyThrows
    @Test
    @WithAnonymousUser
    void commitEdit_permission_exception() {
        String givenComment = randString();
        givenMessage = randString();
        doThrow(new PermissionEvaluationException(givenMessage)).when(wikiPageCommandService).commitUpdate(givenContributorId, givenTitle, givenComment, givenVersionToken, givenContent);
        String body = objectMapper.writeValueAsString(new WikiController.CommitEditRequest(givenComment, givenVersionToken, givenContent));

        // when
        MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/wiki/" + givenTitle + "/edit")
                            .content(body)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorMessage errorMessage = objectMapper.readValue(contentAsString, ErrorMessage.class);
        assertThat(tuple(errorMessage.getStatus(), errorMessage.getMessage(), errorMessage.getPath()))
                .isEqualTo(tuple(HttpStatus.FORBIDDEN.value(), givenMessage, "/api/v1/wiki/" + givenTitle + "/edit"));
    }
}
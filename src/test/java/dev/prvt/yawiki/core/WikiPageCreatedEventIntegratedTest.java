package dev.prvt.yawiki.core;

import dev.prvt.yawiki.core.permission.domain.NamespacePermission;
import dev.prvt.yawiki.core.permission.domain.PagePermission;
import dev.prvt.yawiki.core.permission.domain.repository.PagePermissionRepository;
import dev.prvt.yawiki.core.wikipage.application.WikiPageCommandService;
import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.fixture.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 위키 페이지가 생성되었을 때 발생하는 이벤트를 통하여 적절히 권한 엔티티가 생성되는지 확인하는 테스트
 */
@SpringBootTest(properties = {"yawiki.default-permission.do-initialize=true"})
public class WikiPageCreatedEventIntegratedTest {

    @Autowired
    WikiPageCommandService wikiPageCommandService;

    @Autowired
    WikiPageRepository wikiPageRepository;

    @Autowired
    PagePermissionRepository pagePermissionRepository;

    @ParameterizedTest
    @ValueSource(strings = {"NORMAL", "MAIN", "TEMPLATE"})
    @DisplayName("WikiPage가 생성되면 PagePermission 도 생성돼야함.")
    void create(String namespaceName) {
        // given
        Namespace namespace = Namespace.valueOf(namespaceName);

        UUID givenContributorId = UUID.randomUUID();
        String givenTitle = Fixture.randString();
        WikiPageTitle wikiPageTitle = new WikiPageTitle(givenTitle, namespace);

        // when
        wikiPageCommandService.create(givenContributorId, wikiPageTitle);

        // then

        Optional<WikiPage> wikiPageFound = wikiPageRepository.findByTitleWithRevisionAndRawContent(wikiPageTitle.title(), wikiPageTitle.namespace());
        assertThat(wikiPageFound)
                .describedAs("생성된 WikiPage를 찾아와야함.")
                .isPresent();
        WikiPage wikiPage = wikiPageFound.orElseThrow();


        Optional<PagePermission> pagePermissionFound = pagePermissionRepository.findById(wikiPage.getId());
        assertThat(pagePermissionFound)
                .describedAs("생성된 페이지 권한을 찾아와야함.")
                .isPresent();
        PagePermission pagePermission = pagePermissionFound.orElseThrow();
        assertThat(pagePermission.getPermission())
                .describedAs("생성 직후에는 페이지별 특수 권한이 설정되지 않음.")
                .isNull();

        NamespacePermission namespacePermission = pagePermission.getNamespacePermission();
        assertThat(namespacePermission.getNamespaceId())
                .describedAs("네임스페이스가 적절히 전달되어 생성되어야함.")
                .isEqualTo(namespace.getIntValue());
    }

}

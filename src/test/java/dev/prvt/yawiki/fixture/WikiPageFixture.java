package dev.prvt.yawiki.fixture;

import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.core.wikipage.domain.model.*;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static dev.prvt.yawiki.fixture.Fixture.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WikiPageFixture {
    private static final WikiPageFactory wikiPageFactory = new WikiPageFactory();

    public static RawContent aRawContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(UUID.randomUUID());
        }
        return new RawContent(sb.toString());
    }

    public static WikiPageTitle aWikiPageTitle() {
        return new WikiPageTitle(randString(5, 255), aNamespace());
    }

    public static WikiPage aNormalWikiPage() {
        return wikiPageFactory.create(randString(), Namespace.NORMAL);
    }

    private static final Namespace[] namespaces = Namespace.values();

    public static Namespace aNamespace() {
        return namespaces[random().nextInt(namespaces.length)];
    }

    public static Revision.RevisionBuilder aRevision() {
        return Revision.builder()
                .contributorId(UUID.randomUUID())
                .rawContent(aRawContent())
                .comment(randString());
    }

    public static MemberContributor.MemberContributorBuilder aMemberContributor() {
        return MemberContributor.builder()
                .id(UUID.randomUUID())
                .memberName(UUID.randomUUID().toString());
    }

    public static AnonymousContributor.AnonymousContributorBuilder anAnonymousContributor() {
        return AnonymousContributor.builder()
                .id(UUID.randomUUID())
                .ipAddress(Fixture.aInetV4Address());
    }

    @SneakyThrows
    public static void wikiPageUpdate(WikiPage wikiPage, UUID contributorId, String comment, String content) {
//        Method update = WikiPage.class.getDeclaredMethod("update", UUID.class, String.class, String.class);
//        update.setAccessible(true);
//        update.invoke(wikiPage, contributorId, comment, content);
        wikiPage.update(contributorId, comment, content);
    }

    @SneakyThrows
    public static void setWikiPageId(WikiPage wikiPage, UUID id) {
        Field idField = WikiPage.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(wikiPage, id);
    }

    @SneakyThrows
    public static void setWikiPageLastModifiedBy(WikiPage wikiPage, UUID lastModifiedBy) {
        Field lastModifiedByField = WikiPage.class.getDeclaredField("lastModifiedBy");
        lastModifiedByField.setAccessible(true);
        lastModifiedByField.set(wikiPage, lastModifiedBy);
    }

    @SneakyThrows
    public static void setWikiPageActivated(WikiPage wikiPage, boolean activated) {
        Field activatedField = WikiPage.class.getDeclaredField("activated");
        activatedField.setAccessible(true);
        activatedField.set(wikiPage, activated);
    }

    @SneakyThrows
    public static void setWikiPageActive(WikiPage wikiPage, boolean active) {
        Field activeField = WikiPage.class.getDeclaredField("active");
        activeField.setAccessible(true);
        activeField.set(wikiPage, active);
    }


    public static void updateWikiPageRandomly(WikiPage wikiPage) {
        wikiPageUpdate(wikiPage, UUID.randomUUID(), randString(), randString() + randString() + randString());
    }

    public static void updateWikiPageRandomlyWithContributorId(WikiPage wikiPage, UUID contributorId) {
        wikiPageUpdate(wikiPage, contributorId, randString(), randString() + randString() + randString());
    }

    public static void assertEqualRawContent(RawContent actual, RawContent expected) {
        assertNotNull(actual);
        assertNotNull(expected);

        assertThat(actual.getId()).isNotNull().isEqualTo(expected.getId());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
    }

    public static void assertEqualRevision(Revision actual, Revision expected) {
        assertNotNull(actual);
        assertNotNull(expected);

        assertThat(actual.getComment()).isEqualTo(expected.getComment());
        assertThat(actual.getRevVersion()).isEqualTo(expected.getRevVersion());
        assertThat(actual.getWikiPage().getId()).isEqualTo(expected.getWikiPage().getId());
        assertThat(actual.getRawContent().getId()).isEqualTo(expected.getRawContent().getId());
    }

    public static void assertEqualWikiPage(WikiPage actual, WikiPage expected) {
        assertNotNull(actual);
        assertNotNull(expected);

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
    }
}

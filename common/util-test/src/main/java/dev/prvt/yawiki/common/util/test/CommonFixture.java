package dev.prvt.yawiki.common.util.test;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static dev.prvt.yawiki.common.util.test.Fixture.random;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;

public class CommonFixture {
    public static WikiPageTitle aWikiPageTitle() {
        return new WikiPageTitle(randString(5, 255), aNamespace());
    }
    public static Namespace aNamespace() {
        return namespaces[random().nextInt(namespaces.length)];
    }
    private static final Namespace[] namespaces = Namespace.values();

}

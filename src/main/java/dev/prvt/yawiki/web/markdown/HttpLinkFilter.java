package dev.prvt.yawiki.web.markdown;

import java.util.regex.Pattern;

/**
 * http 링크와 일치하는 경우, 내부 참조로 처리되어선 안 됨.
 */
public class HttpLinkFilter implements WikiReferenceFilter {
    private final Pattern compiled = Pattern.compile("http[s]?://[^\\s]+");

    public boolean isWikiReference(String referenced) {
        return !compiled.matcher(referenced).matches();
    }
}

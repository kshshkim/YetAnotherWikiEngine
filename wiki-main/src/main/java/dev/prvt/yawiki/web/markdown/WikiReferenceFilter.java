package dev.prvt.yawiki.web.markdown;

/**
 * 위키 문법상 내부 링크로 판별되지만, 실제 내부 링크가 아닌 경우(ex. 외부 링크와 내부 링크의 문법이 동일한 나무위키 문법)를 판별하여 필터링
 */
public interface WikiReferenceFilter {
    boolean isWikiReference(String referenced);
}

package dev.prvt.yawiki.web.markdown;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverter;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 나무위키 문법에 해당하는 내부 위키 제목 참조 목록을 추출함. 내부 참조 관리 목적으로 추출하는 것이기 때문에 참조 문서의 제목 이외 다른 마크다운은 파싱할 필요가 없음.
 *
 * <li>
 * [[제목]] -> '제목'
 * </li>
 * <li>
 * [[진짜 제목|보이는 제목]] -> '진짜 제목'
 * </li>
 */
public class ReferencedTitleRegexExtractor implements ReferencedTitleExtractor {

    /**
     * [[와 ]] 사이의 내용물을 추출함. 이스케이프 처리된 경우엔 무시함.
     */
    private static final Pattern referencePattern = Pattern.compile("\\[\\[([^\\]]*?(?:\\\\\\][^\\]]*?)*?)]]");

    /**
     * 가장 먼저 등장하는 파이프 문자를 찾음. 이스케이프 처리된 경우엔 무시함.
     */
    private static final Pattern pipePattern = Pattern.compile("(?<!\\\\)\\|");

    /**
     * http 링크 매칭 여부 판정
     */
    private static final Pattern externalLinkPattern = Pattern.compile("^http[s]?://[^\\s]+");

    private final WikiPageTitleConverter converter;

    public ReferencedTitleRegexExtractor(
        WikiPageTitleConverter converter
    ) {
        this.converter = converter;
    }
    /**
     * 보이는 제목과 링크된 문서의 제목이 다른 참조에서 링크된 문서의 제목을 추출하여 반환함.
     * ex) "real title|displayed title" -> { "real title", "displayed title" } -> "real title"
     */
    private String removeDisplayedTitle(String title) {
        String[] split = pipePattern.split(title, 2); // 가장 처음 등장하는 |를 기준으로 나누기 때문에 limit=2
        return split.length > 0 ? split[0] : ""; // 문법상 첫번째 덩어리가 진짜 제목. split 길이가 0인 경우는 title.equals("|") 인 경우 뿐임.
    }

    /**
     * 문단 앵커를 제거함.
     * ex) "title#s-11" -> "title"
     */
    private String removeParagraphAnchor(String title) {
        int hashIdx = title.lastIndexOf("#");
        if (hashIdx != -1) {
            if (hashIdx == 0 || title.charAt(hashIdx - 1) != '\\') {
                return title.substring(0, hashIdx);
            }
        }
        return title;
    }

    /**
     * 이스케이프 처리된 제목을 복원함.
     * ex) "\#해시태그" -> "#해시태그"
     */
    private String recoverEscaped(String title) {
        return title
                   .replace("\\|", "|")
                   .replace("\\#", "#")
                   .replace("\\[", "[")
                   .replace("\\]", "]")
                   .replace("\\\\", "\\")
            ;
    }

    @Override
    public Set<WikiPageTitle> extractReferencedTitles(String rawMarkDown) {
        Matcher matcher = referencePattern.matcher(rawMarkDown);
        return matcher.results()
                   .map(m -> m.group(1))
                   .filter(externalLinkPattern.asPredicate().negate())  // 외부 링크 필터링
                   .map(this::removeDisplayedTitle)  // 보이는 제목과 링크된 문서의 제목이 다른 참조에서 링크된 문서의 제목을 추출
                   .map(this::removeParagraphAnchor)  // # 기호로 표시된 문단 앵커 제거
                   .map(this::recoverEscaped)  // 이스케이프 처리된 제목 복원
                   .filter(t -> !t.isBlank())  // 빈 제목 필터링
                   .map(converter::convert)  // WikiPageTitle 클래스로 매핑
                   .collect(Collectors.toUnmodifiableSet());
    }
}

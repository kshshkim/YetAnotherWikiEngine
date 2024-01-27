package dev.prvt.yawiki.titleexistence.web.api;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.titleexistence.cache.application.WikiPageTitleExistenceFilter;
import dev.prvt.yawiki.titleexistence.web.api.response.TitleListResponse;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/title")
@RequiredArgsConstructor
public class TitleController {

    private final WikiPageTitleExistenceFilter filter;

    private final WikiPageTitleConverter wikiPageTitleConverter;

    public record TitleExistenceRequest(
        List<String> titles
    ) {
    }

    private Stream<WikiPageTitle> convertToWikiPageStream(
        TitleExistenceRequest titleExistenceRequest
    ) {
        return titleExistenceRequest.titles().stream()
                   .map(wikiPageTitleConverter::convert);
    }

    /**
     * <p>Get 요청에 Body 를 포함시키는 것은 비표준적인 방법이지만, 100개 이상의 파라미터를 url에 포함시키는 것은 굉장히 비효율적이기 때문에 타협함.
     * <br>ngrinder 등 일부 클라이언트에서 Get 요청 Body 를 지원하지 않는 경우가 있음. 아래의 Post 요청을 활용할 수 있음.</p>
     *
     * <p>여러 WikiPageTitle 중, 존재하지 않는 제목을 반환함. 위키 문서가 참조중인 문서 제목 대부분은 존재하기 때문에, 존재하지 않는 제목을 반환하는 것이 보다 효율적임.</p>
     *
     * @param request "틀:제목" 형태의 문자열 파라미터. {@link WikiPageTitle} 형태로 자동 변환되지 않음.
     * @return 존재하지 <b>않는</b> 제목 목록 {@link TitleListResponse}
     */
    @GetMapping("/nonexistent")
    public TitleListResponse getNonExistTitlesWithRequestBody(
        @RequestBody TitleExistenceRequest request
    ) {
        return TitleListResponse.from(
            filter.getNonExistentTitles(
                convertToWikiPageStream(request)
            )
        );
    }

    /**
     * <p><b>조회 요청</b>을 POST 메서드로 수행하는 것은 비표준적인 방법이지만, GET 요청으로 100개 이상의 파라미터를 url에 포함시키는 것은 굉장히 비효율적이기 때문에 타협함.</p>
     *
     * <p>여러 WikiPageTitle 중, 존재하지 않는 제목을 반환함. 위키 문서가 참조중인 문서 제목 대부분은 존재하기 때문에, 존재하지 않는 제목을 반환하는 것이 보다 효율적임.</p>
     *
     * @param request "틀:제목" 형태의 문자열 파라미터. {@link WikiPageTitle} 형태로 자동 변환되지 않음.
     * @return 존재하지 <b>않는</b> 제목 목록 {@link TitleListResponse}
     */
    @PostMapping("/nonexistent")
    public TitleListResponse getNonExistentTitlesWithPostRequest(
        @RequestBody TitleExistenceRequest request
    ) {
        return TitleListResponse.from(
            filter.getNonExistentTitles(
                convertToWikiPageStream(request)
            )
        );
    }
}

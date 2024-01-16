package dev.prvt.yawiki.web.api.v1.title;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.existence.WikiPageTitleExistenceChecker;
import dev.prvt.yawiki.web.api.v1.title.response.TitleListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/title")
@RequiredArgsConstructor
public class TitleController {

    private final WikiPageTitleExistenceChecker titleExistenceChecker;

    /**
     * 여러 WikiPageTitle 중, 존재하지 않는 제목을 반환함. 위키 문서가 참조중인 문서 제목 대부분은 존재하기 때문에, 존재하지 않는 제목을 반환하는 것이 보다 효율적임.
     * @param titles "틀:제목" 형태의 문자열 파라미터가 등록된 컨버터를 통해 WikiPageTitle 로 자동 변환됨.
     * @return 존재하지 <b>않는</b> 제목 목록 {@link TitleListResponse}
     */
    @GetMapping("/filter/nonexistent")
    public TitleListResponse getNonExistTitles(
            @RequestParam("t") Collection<WikiPageTitle> titles  // 요청 url 이 과도하게 길어지는 것을 방지하기 위해 t 한 글자로 받음.
    ) {
        return TitleListResponse.from(
                titleExistenceChecker.filterExistentTitles(titles)
        );
    }

}

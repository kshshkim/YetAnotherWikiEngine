package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.springframework.core.convert.converter.Converter;

public interface WikiPageTitleConverter extends Converter<String, WikiPageTitle> {
    /**
     * <p>구분자가 포함된 문자열을 받아 WikiPageTitle 반환.</p>
     * <p>ex) "틀: 아무개" -> WikiPageTitle[title="아무개", Namespace=TEMPLATE]</p>
     * @param source
     * @return
     */
    WikiPageTitle convert(String source);
}

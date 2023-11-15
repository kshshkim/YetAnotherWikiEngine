package dev.prvt.yawiki.web.api.v1.wiki;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import org.springframework.stereotype.Component;

/**
 * 타이틀을 파싱하여 적절한 Namespace enum을 반환함.
 * @see dev.prvt.yawiki.core.wikipage.domain.model.Namespace
 */
@Component
public class NamespaceParser {  // todo test
    public Namespace parseTitle(String title) {
        String[] split = title.split(":");
        if (split.length == 1) {
            return Namespace.NORMAL;
        }
        return switch (split[0]) {      // todo property 클래스로 분리, 국제화
            case "파일" -> Namespace.FILE;
            case "대문" -> Namespace.MAIN;
            case "분류" -> Namespace.CATEGORY;
            case "틀" -> Namespace.TEMPLATE;
            default -> Namespace.NORMAL;
        };
    }
}

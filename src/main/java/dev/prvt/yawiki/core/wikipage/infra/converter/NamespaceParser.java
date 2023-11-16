package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;

import java.util.Map;

/**
 * 타이틀을 파싱하여 적절한 Namespace enum을 반환함.
 * @see dev.prvt.yawiki.core.wikipage.domain.model.Namespace
 */
public class NamespaceParser {
    private final Map<String, Namespace> identifierMap;

    public NamespaceParser(Map<String, Namespace> identifierMap) {
        this.identifierMap = identifierMap;
    }

    private Namespace getNamespaceFromIdentifier(String identifier) {
        return identifierMap.getOrDefault(identifier, Namespace.NORMAL);
    }


    /**
     * 첫번째 ':'을 구분자로, 이전의 문자열이 identifierMap 에 존재하는 경우 해당 Namespace 반환. 존재하지 않으면 기본값 반환.
     * @param title
     * @return
     */
    public Namespace getNamespace(String title) {
        String[] split = title.split(":");

        if (split.length == 1) {  // 구분자를 포함하지 않는 경우
            return Namespace.NORMAL;
        }

        String identifier = split[0].strip();
        return getNamespaceFromIdentifier(identifier);
    }
}

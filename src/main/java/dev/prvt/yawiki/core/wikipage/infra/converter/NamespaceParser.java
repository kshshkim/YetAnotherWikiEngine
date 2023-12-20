package dev.prvt.yawiki.core.wikipage.infra.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * 문자열을 파싱하여 적절한 Namespace enum을 반환함.
 *
 * @see dev.prvt.yawiki.core.wikipage.domain.model.Namespace
 */
public class NamespaceParser {
    /**
     * 구분자-네임스페이스 Map
     */
    private final Map<String, Namespace> identifierMap;

    /**
     * 구분자 기본값으로 Namespace.name() 사용.
     */
    public NamespaceParser() {
        this.identifierMap = Arrays
                .stream(Namespace.values())
                .filter(namespace -> !namespace.equals(Namespace.NORMAL))  // NORMAL 구분자 정의되면 안 됨.
                .collect(toMap(
                        Namespace::name,
                        namespace -> namespace
                ));
    }

    /**
     * 기본 구분자 이외의 추가 구분자를 주입받는 메서드.
     * @param identifierMap 커스텀 네임스페이스 구분자 맵.
     */
    public NamespaceParser(Map<String, Namespace> identifierMap) {
        this();
        this.identifierMap.putAll(identifierMap);
    }

    /**
     * @param identifier 네임스페이스 구분자
     * @return 정의된 구분자인 경우 해당 Namespace. 정의되지 않은 경우 NORMAL(기본) 반환.
     */
    private Namespace getNamespaceFromIdentifier(String identifier) {
        return identifierMap.getOrDefault(identifier, Namespace.NORMAL);
    }

    /**
     * 첫번째 ':'을 구분자로, 이전의 문자열이 identifierMap 에 존재하는 경우 해당 Namespace 반환. 존재하지 않으면 기본값 반환.
     * @param title 파싱되지 않은 문자열 제목
     * @return Namespace
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

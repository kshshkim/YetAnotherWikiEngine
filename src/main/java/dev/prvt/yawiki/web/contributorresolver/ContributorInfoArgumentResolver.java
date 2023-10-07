package dev.prvt.yawiki.web.contributorresolver;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * SecurityContext 에서 가져오는 Authentication 객체를 기반으로 사용자 정보를 반환함.
 * ConversionService 와 유사하게 작동함. ConversionService 를 주입할 경우 설정 과정에서 순환참조 문제가 터지기 때문에 일단 분리함.
 * 당장은 2가지 뿐이라 성능 문제가 거의 없지만, 추후 필요할 경우 결과를 캐시하는 등 최적화 필요.
 */
public class ContributorInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<ContributorInfoConverter> converters;


    public void addConverter(ContributorInfoConverter converter) {
        duplicateCheck(converter);
        converters.add(converter);
    }

    private void duplicateCheck(ContributorInfoConverter converter) {
        boolean duplicate = converters.stream()
                .anyMatch(conv -> conv.getClass() == converter.getClass());
        if (duplicate) {
            throw new IllegalArgumentException("duplicate converter has been registered");
        }
    }

    private ContributorInfoConverter getConverter(Authentication authentication) {
        return this.converters.stream()
                .filter(converter -> converter.supports(authentication))
                .findAny()
                .orElseThrow(() -> new RuntimeException("ContributorInfoConverter not implemented: " + authentication.getClass()));
    }

    private ContributorInfoArg convert(Authentication authentication) {
        return getConverter(authentication).convert(authentication);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ContributorInfo.class);
    }
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return convert(SecurityContextHolder.getContext().getAuthentication());
    }

    public ContributorInfoArgumentResolver(@NotNull List<ContributorInfoConverter> converters) {
        this.converters = converters;
    }

    public ContributorInfoArgumentResolver() {
        this.converters = new ArrayList<>();
    }
}

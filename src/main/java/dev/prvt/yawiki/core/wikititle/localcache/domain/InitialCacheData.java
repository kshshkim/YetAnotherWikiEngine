package dev.prvt.yawiki.core.wikititle.localcache.domain;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * @param stream 스트림
 * @param totalElements 총 내용물의 숫자(초기 버킷 사이즈 설정 등에 필요)
 * @param lastUpdatedAt 캐시 정보 일자
 * @param <T> 내용물의 타입
 */
public record InitialCacheData<T>(
        Stream<T> stream,
        int totalElements,
        LocalDateTime lastUpdatedAt
) {
}

package dev.prvt.yawiki.core.wikititle.cache.domain.updater;

import java.util.List;

/**
 * <ul>
 *     요구사항
 *     <li>
 *         일정 기간(트랜잭션 timeout) 내에 중복된 RemoteChangeLog 를 write 하더라도, 정상적으로 존재 여부 값이 유지되어야함.
 *         Set 기반 구현인 경우 중복 실행하더라도 문제가 없지만, Counting Bloom Filter 를 활용하는 경우, 작업이 중복 수행되지 않도록 내부적으로 조치를 취해야함.
 *     </li>
 * </ul>
 */
public interface CacheWriter {
    void write(List<RemoteChangeLog> logs);
}

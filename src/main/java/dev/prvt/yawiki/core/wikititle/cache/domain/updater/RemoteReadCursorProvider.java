package dev.prvt.yawiki.core.wikititle.cache.domain.updater;

import java.time.LocalDateTime;

/**
 * 변동 내역을 어디부터 어디까지 읽어올지 결정하는 커서. 구현시 transaction timeout, 중복 삽입/삭제 가능 여부를 고려하여 마진을 주어야함.
 */
public interface RemoteReadCursorProvider {

    record ReadCursor(
        LocalDateTime after,
        LocalDateTime before
    ) {
    }

    ReadCursor getReadCursor();
}

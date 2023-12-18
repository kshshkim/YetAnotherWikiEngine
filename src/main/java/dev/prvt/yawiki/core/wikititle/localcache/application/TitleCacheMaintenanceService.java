package dev.prvt.yawiki.core.wikititle.localcache.application;

import dev.prvt.yawiki.core.wikititle.localcache.domain.initializer.LocalCacheInitializer;
import dev.prvt.yawiki.core.wikititle.localcache.domain.updater.LocalCacheUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
public class TitleCacheMaintenanceService {
    private final LocalCacheInitializer localCacheInitializer;
    private final LocalCacheUpdater localCacheUpdater;

    /**
     * 이전 업데이트 작업이 완료되고 3초가 지나면 다시 시도함.
     */
    @Scheduled(fixedDelayString = "${yawiki.local-cache.fixed-delay:3000}")
    public void updateCache() {
        try {
            localCacheUpdater.update();
            log.debug("local cache updated");
        } catch (IllegalStateException e) {
            log.debug(e.getMessage());
        }

    }

    @EventListener(ApplicationReadyEvent.class)
    public void initCache() {
        localCacheInitializer.initialize();
        log.debug("local cache initialized");
    }
}

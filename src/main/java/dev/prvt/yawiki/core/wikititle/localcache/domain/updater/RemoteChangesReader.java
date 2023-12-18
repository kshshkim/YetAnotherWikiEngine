package dev.prvt.yawiki.core.wikititle.localcache.domain.updater;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RemoteChangesReader {
    private final RemoteChangesRepository remoteChangesRepository;

    public RemoteChangesReader(RemoteChangesRepository remoteChangesRepository) {
        this.remoteChangesRepository = remoteChangesRepository;
    }

    /**
     * @param after 이 시점 이후의 변동 내역을 읽어옴. (exclusive)
     * @param before 이 시점 이전의 변동 내역을 읽어옴. (exclusive)
     * @return 제목 변동 내역 Slice
     */
    public List<RemoteChangeLog> readUpdated(LocalDateTime after, LocalDateTime before) {
        return remoteChangesRepository.findRemoteChangesByCursor(after, before);
    }
}

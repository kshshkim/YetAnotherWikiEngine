package dev.prvt.yawiki.core.wikititle.history.application;

import dev.prvt.yawiki.common.model.TitleUpdateType;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistoryRepository;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistory;
import dev.prvt.yawiki.core.wikititle.history.domain.TitleHistoryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class TitleHistoryService {
    private final TitleHistoryRepository repository;
    private final TitleHistoryFactory factory;

    public TitleHistory append(WikiPageTitle wikiPageTitle, TitleUpdateType titleUpdateType, LocalDateTime timestamp) {
        return repository.save(
                factory.create(
                        wikiPageTitle,
                        TitleUpdateType.CREATED,
                        LocalDateTime.now()
                )
        );
    }
}

package dev.prvt.yawiki.core.wikipage.infra.repository;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static dev.prvt.yawiki.fixture.WikiPageFixture.setWikiPageId;

public class WikiPageMemoryRepository implements WikiPageRepository {
    static private final ConcurrentMap<WikiPageTitle, WikiPage> titleStore = new ConcurrentHashMap<>();
    static private final ConcurrentMap<UUID, WikiPage> idStore = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public WikiPage save(WikiPage entity) {

        if (entity.getId() != null) {
            throw new IllegalStateException("엔티티가 이미 영속화된 상태임.");
        }

        if (titleStore.containsKey(entity.getWikiPageTitle())) {
            throw new IllegalStateException("제목이 중복됨.");
        }

        setWikiPageId(entity, UUID.randomUUID());

        titleStore.put(entity.getWikiPageTitle(), entity);
        idStore.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<WikiPage> findByTitleAndNamespace(String title, Namespace namespace) {
        return Optional.ofNullable(titleStore.get(new WikiPageTitle(title, namespace)));
    }

    @Override
    public Optional<WikiPage> findById(UUID uuid) {
        return Optional.ofNullable(idStore.get(uuid));
    }
}

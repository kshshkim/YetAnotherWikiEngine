package dev.prvt.yawiki.core.wikipage.infra.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WikiPageMemoryRepository implements WikiPageRepository {
    static private final ConcurrentMap<String, WikiPage> titleStore = new ConcurrentHashMap<>();
    static private final ConcurrentMap<UUID, WikiPage> idStore = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public WikiPage save(WikiPage entity) {

        if (entity.getId() != null) {
            throw new IllegalStateException("엔티티가 이미 영속화된 상태임.");
        }

        if (titleStore.containsKey(entity.getTitle())) {
            throw new IllegalStateException("제목이 중복됨.");
        }

        Class<? extends WikiPage> aClass = entity.getClass();
        Field id = aClass.getDeclaredField("id");
        id.setAccessible(true);
        id.set(entity, UUID.randomUUID());  // 리플렉션으로 ID 설정

        titleStore.put(entity.getTitle(), entity);
        idStore.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<WikiPage> findByTitle(String title) {
        return Optional.ofNullable(titleStore.get(title));
    }

    @Override
    public Optional<WikiPage> findById(UUID uuid) {
        return Optional.ofNullable(idStore.get(uuid));
    }

    @Override
    public Optional<WikiPage> findByTitleWithRevisionAndRawContent(String title) {
        return findByTitle(title);
    }

    @Override
    public WikiPage findOrCreate(String title) {
        return findByTitle(title)
                .orElseGet(() -> save(WikiPage.create(title)));
    }
}

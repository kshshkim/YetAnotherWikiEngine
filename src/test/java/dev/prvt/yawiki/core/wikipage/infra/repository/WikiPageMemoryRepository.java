package dev.prvt.yawiki.core.wikipage.infra.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import lombok.SneakyThrows;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class WikiPageMemoryRepository implements WikiPageRepository {
    static private final ConcurrentMap<String, WikiPage> titleStore = new ConcurrentHashMap<>();
    static private final ConcurrentMap<UUID, WikiPage> idStore = new ConcurrentHashMap<>();
    @Override
    @SneakyThrows
    public <S extends WikiPage> S save(S entity) {
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

    // 이하 미구현

    @Override
    public Optional<WikiPage> findByTitleWithRevisionAndRawContent(String title) {
        return Optional.empty();
    }

    @Override
    public List<WikiPage> findAll() {
        return null;
    }

    @Override
    public List<WikiPage> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<WikiPage> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<WikiPage> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(WikiPage entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends WikiPage> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends WikiPage> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends WikiPage> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends WikiPage> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<WikiPage> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public WikiPage getOne(UUID uuid) {
        return null;
    }

    @Override
    public WikiPage getById(UUID uuid) {
        return null;
    }

    @Override
    public WikiPage getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends WikiPage> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends WikiPage> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends WikiPage> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends WikiPage> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends WikiPage> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends WikiPage> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends WikiPage, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}

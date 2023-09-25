package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.ResourcePermission;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class ResourcePermissionMemoryRepository implements ResourcePermissionRepository{
    static private final ConcurrentMap<UUID, ResourcePermission> idStore = new ConcurrentHashMap<>();
    @Override
    public <S extends ResourcePermission> S save(S entity) {
        idStore.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<ResourcePermission> findById(UUID uuid) {
        return Optional.ofNullable(idStore.get(uuid));
    }

    // 이하 미구현
    @Override
    public List<ResourcePermission> findAll() {
        return null;
    }

    @Override
    public List<ResourcePermission> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<ResourcePermission> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<ResourcePermission> findAllById(Iterable<UUID> uuids) {
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
    public void delete(ResourcePermission entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends ResourcePermission> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends ResourcePermission> List<S> saveAll(Iterable<S> entities) {
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
    public <S extends ResourcePermission> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ResourcePermission> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<ResourcePermission> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public ResourcePermission getOne(UUID uuid) {
        return null;
    }

    @Override
    public ResourcePermission getById(UUID uuid) {
        return null;
    }

    @Override
    public ResourcePermission getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends ResourcePermission> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ResourcePermission> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends ResourcePermission> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends ResourcePermission> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ResourcePermission> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ResourcePermission> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ResourcePermission, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}

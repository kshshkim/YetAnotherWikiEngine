package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.AuthorityProfile;
import lombok.SneakyThrows;
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

public class AuthorityProfileMemoryRepository implements AuthorityProfileRepository{
    static private final ConcurrentMap<UUID, AuthorityProfile> idStore = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public <S extends AuthorityProfile> S save(S entity) {
        idStore.put(entity.getId(), entity);
        return entity;
    }
    @Override
    public Optional<AuthorityProfile> findById(UUID uuid) {
        return Optional.ofNullable(idStore.get(uuid));
    }

    // 이하 미구현
    @Override
    public List<AuthorityProfile> findAll() {
        return null;
    }

    @Override
    public List<AuthorityProfile> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<AuthorityProfile> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<AuthorityProfile> findAllById(Iterable<UUID> uuids) {
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
    public void delete(AuthorityProfile entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends AuthorityProfile> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends AuthorityProfile> List<S> saveAll(Iterable<S> entities) {
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
    public <S extends AuthorityProfile> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends AuthorityProfile> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<AuthorityProfile> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public AuthorityProfile getOne(UUID uuid) {
        return null;
    }

    @Override
    public AuthorityProfile getById(UUID uuid) {
        return null;
    }

    @Override
    public AuthorityProfile getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends AuthorityProfile> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends AuthorityProfile> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends AuthorityProfile> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends AuthorityProfile> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends AuthorityProfile> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends AuthorityProfile> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends AuthorityProfile, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}

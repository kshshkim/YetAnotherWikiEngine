package dev.prvt.yawiki.auth.member.domain;

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

public class MemberMemoryRepository implements MemberRepository {
    static private final ConcurrentMap<String, Member> usernameStore = new ConcurrentHashMap<>();
    static private final ConcurrentMap<String, Member> displayedNameStore = new ConcurrentHashMap<>();
    static private final ConcurrentMap<UUID, Member> idStore = new ConcurrentHashMap<>();
    @Override
    public <S extends Member> S save(S entity) {
        if (usernameStore.containsKey(entity.getUsername())) {
            throw new IllegalStateException("제목이 중복됨.");
        }

        if (displayedNameStore.containsKey(entity.getUsername())) {
            throw new IllegalStateException("제목이 중복됨.");
        }

        usernameStore.put(entity.getUsername(), entity);
        displayedNameStore.put(entity.getDisplayedName(), entity);
        idStore.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return Optional.ofNullable(usernameStore.get(username));
    }

    @Override
    public Optional<Member> findById(UUID uuid) {
        return Optional.ofNullable(idStore.get(uuid));
    }


    // 이하 미구현
    @Override
    public List<Member> findAll() {
        return null;
    }

    @Override
    public List<Member> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Member> findAllById(Iterable<UUID> uuids) {
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
    public void delete(Member entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends Member> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Member> List<S> saveAll(Iterable<S> entities) {
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
    public <S extends Member> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Member> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Member> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Member getOne(UUID uuid) {
        return null;
    }

    @Override
    public Member getById(UUID uuid) {
        return null;
    }

    @Override
    public Member getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends Member> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Member> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Member> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Member> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Member> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Member> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Member, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}

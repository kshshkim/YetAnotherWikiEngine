package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.member.application.MemberJoinEvent;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileMemoryRepository;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class PermissionMemberJoinEventHandlerTest {
    PermissionGroupRepository dummyPermissionGroupRepository = new PermissionGroupRepository() {
        @Override
        public List<PermissionGroup> findAll() {
            return null;
        }

        @Override
        public List<PermissionGroup> findAll(Sort sort) {
            return null;
        }

        @Override
        public List<PermissionGroup> findAllById(Iterable<UUID> uuids) {
            return null;
        }

        @Override
        public <S extends PermissionGroup> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends PermissionGroup> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends PermissionGroup> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<PermissionGroup> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public PermissionGroup getOne(UUID uuid) {
            return null;
        }

        @Override
        public PermissionGroup getById(UUID uuid) {
            return null;
        }

        @Override
        public PermissionGroup getReferenceById(UUID uuid) {
            return new PermissionGroup(uuid, null, null);
        }

        @Override
        public <S extends PermissionGroup> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends PermissionGroup> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public Page<PermissionGroup> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends PermissionGroup> S save(S entity) {
            return null;
        }

        @Override
        public Optional<PermissionGroup> findById(UUID uuid) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(UUID uuid) {
            return false;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(UUID uuid) {

        }

        @Override
        public void delete(PermissionGroup entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {

        }

        @Override
        public void deleteAll(Iterable<? extends PermissionGroup> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public <S extends PermissionGroup> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends PermissionGroup> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends PermissionGroup> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends PermissionGroup> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends PermissionGroup, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }
    };
    UUID givenDefaultGroupId = UUID.randomUUID();
    DefaultPermissionProperties defaultPermissionProperties = new DefaultPermissionProperties(
            0, 0, 0, 0, 3, givenDefaultGroupId
    );
    AuthorityProfileRepository memoryAuthorityProfileRepository = new AuthorityProfileMemoryRepository();
    PermissionMemberJoinEventHandler permissionMemberJoinEventHandler = new PermissionMemberJoinEventHandler(memoryAuthorityProfileRepository, dummyPermissionGroupRepository, defaultPermissionProperties);

    @Test
    void handle() {
        // given
        MemberJoinEvent memberJoinEvent = new MemberJoinEvent(UUID.randomUUID(), randString());
        // when
        permissionMemberJoinEventHandler.handle(memberJoinEvent);
        // then
        Optional<AuthorityProfile> found = memoryAuthorityProfileRepository.findById(memberJoinEvent.memberId());
        assertThat(found).isNotEmpty();
        AuthorityProfile authorityProfile = found.get();

        GrantedGroupAuthority grantedGroupAuthority = authorityProfile.getGroupAuthorities().get(0);
        assertThat(grantedGroupAuthority.getAuthorityLevel())
                .describedAs("기본 권한은 1")
                .isEqualTo(1);
        assertThat(grantedGroupAuthority.getGroup().getId())
                .describedAs("property 설정값 따라서 설정돼야함.")
                .isEqualTo(givenDefaultGroupId);
    }


}
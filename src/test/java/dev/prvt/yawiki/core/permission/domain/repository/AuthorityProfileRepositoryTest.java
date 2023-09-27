package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.permission.domain.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.PermissionGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthorityProfileRepositoryTest {
    @Autowired
    AuthorityProfileRepository authorityProfileRepository;

    @Autowired
    DefaultPermissionProperties defaultPermissionProperties;
    @Autowired
    EntityManager em;
    @Test
    void save_should_success() {
        AuthorityProfile authorityProfile = AuthorityProfile.createWithGroup(UUID.randomUUID(), new PermissionGroup(defaultPermissionProperties.getDefaultPermissionGroupId(), null, null), 2);
        authorityProfileRepository.saveAndFlush(authorityProfile);
    }

    @Test
    void isNew_should_return_false_after_persist() {
        AuthorityProfile given = AuthorityProfile.createWithGroup(UUID.randomUUID(), new PermissionGroup(defaultPermissionProperties.getDefaultPermissionGroupId(), null, null), 2);
        authorityProfileRepository.saveAndFlush(given);
        assertThat(given.isNew())
                .isFalse();

        em.clear();

        AuthorityProfile found = authorityProfileRepository.findById(given.getId()).orElseThrow();
        assertThat(found.isNew())
                .isFalse();
    }
}
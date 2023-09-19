package dev.prvt.yawiki.core.permission.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PermissionRepositoryTest {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void init() {
        Permission build = Permission.builder()
                .create(1)
                .read(0)
                .update(1)
                .delete(1)
                .manage(4)
                .build();

        permissionRepository.save(build);
    }

    @Test
    @Transactional
    void findByAllAttributes() {
        em.flush();
        em.clear();

        Permission found = permissionRepository.findByAllAttributes(1, 0, 1, 1, 4).orElseThrow();

        assertThat(found)
                .isNotNull();

        assertThat(tuple(found.getCreate(), found.getRead(), found.getUpdate(), found.getDelete(), found.getManage()))
                .isEqualTo(tuple(1, 0, 1, 1, 4));
    }

    @Test
    @Transactional
    void uniqueIndexTest() {
        em.flush();
        em.clear();
        init();
        assertThatThrownBy(() -> em.flush())
                .describedAs("unique index should be applied correctly")
                .hasMessageContaining("ConstraintViolationException");
    }
}
package dev.prvt.yawiki.auth.member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    PasswordHasher passwordHasher;

    Member givenMember;
    @BeforeEach
    void init() {
        givenMember = Member.create(UUID.randomUUID(), randString(), randString(), passwordHasher);
        memberRepository.save(givenMember);
        em.flush();
        em.clear();
    }

    @Test
    @Transactional
    void findByUsername() {
        Optional<Member> found = memberRepository.findByUsername(givenMember.getUsername());
        assertThat(found)
                .isNotEmpty();
        Member foundMember = found.orElseThrow();
        assertThat(foundMember.getId())
                .isEqualTo(givenMember.getId());
    }
}
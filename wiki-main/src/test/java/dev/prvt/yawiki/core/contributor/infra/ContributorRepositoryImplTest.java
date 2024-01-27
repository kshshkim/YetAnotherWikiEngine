package dev.prvt.yawiki.core.contributor.infra;

import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.fixture.WikiPageFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.prvt.yawiki.common.util.test.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
@SpringBootTest
class ContributorRepositoryImplTest {
    @Autowired
    ContributorRepositoryImpl contributorRepository;

    @Autowired
    EntityManager em;

    List<Contributor> givenContributors;

    @BeforeEach
    void init() {
        givenContributors = IntStream.range(0, 10)
                .mapToObj(i -> (Contributor) WikiPageFixture.aMemberContributor().build())
                .toList();
        givenContributors.forEach(em::persist);
        em.flush();
        em.clear();
    }

    @Test
    void findContributorsById() {
        List<Tuple> givenTuples = givenContributors.stream().map(contributor -> tuple(contributor.getId(), contributor.getName(), contributor.getState())).toList();

        Stream<Contributor> found = contributorRepository.findContributorsByIds(givenContributors.stream().map(Contributor::getId).toList());
        List<Tuple> foundTuples = found.map(contributor -> tuple(contributor.getId(), contributor.getName(), contributor.getState())).toList();

        assertThat(foundTuples)
                .containsExactlyInAnyOrderElementsOf(givenTuples);
    }

    @Test
    void save_MemberContributor() {
        MemberContributor given = WikiPageFixture.aMemberContributor().build();
        contributorRepository.save(given);
        em.flush();
        em.clear();

        // then
        Optional<Contributor> found = contributorRepository.findById(given.getId());

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getName()).isEqualTo(given.getName());
    }

    @Test
    void save_AnonymousContributor() {
        AnonymousContributor given = WikiPageFixture.anAnonymousContributor().build();
        contributorRepository.save(given);
        em.flush();
        em.clear();

        // then
        Optional<Contributor> found = contributorRepository.findById(given.getId());

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getName()).isEqualTo(given.getName());
    }

    @Test
    void getByInetAddress_when_not_exist() {
        // given
        InetAddress addr = aInetV4Address();
        // when
        Contributor created = contributorRepository.getByInetAddress(addr);
        em.flush();
        em.clear();
        // then
        Optional<Contributor> byId = contributorRepository.findById(created.getId());
        assertThat(byId).isPresent();
        AnonymousContributor contributor = (AnonymousContributor) byId.get();
        assertThat(contributor.getIpAddress())
                .isEqualTo(addr);
    }

    @Test
    void getByInetAddress_when_exist() {
        // given
        InetAddress addr = aInetV4Address();
        AnonymousContributor built = AnonymousContributor.builder()
                .id(UUID.randomUUID())
                .ipAddress(addr)
                .build();
        em.persist(built);
        em.flush();
        em.clear();

        // when
        Contributor byInetAddress = contributorRepository.getByInetAddress(addr);

        assertThat(byInetAddress)
                .isNotNull()
                .isInstanceOf(AnonymousContributor.class);

        assertThat(byInetAddress.getId())
                .isEqualTo(built.getId());



    }
}
package dev.prvt.yawiki.core.contributor.infra;

import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.prvt.yawiki.Fixture.*;
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
                .mapToObj(i -> (Contributor) aMemberContributor().build())
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
        MemberContributor given = aMemberContributor().build();
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
        AnonymousContributor given = anAnonymousContributor().build();
        contributorRepository.save(given);
        em.flush();
        em.clear();

        // then
        Optional<Contributor> found = contributorRepository.findById(given.getId());

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getName()).isEqualTo(given.getName());
    }
}
package dev.prvt.yawiki.core.contributor.application;

import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.prvt.yawiki.common.testutil.Fixture.aInetV4Address;
import static org.assertj.core.api.Assertions.assertThat;

class ContributorApplicationServiceTest {
    ContributorRepository contributorRepository = new ContributorRepository() {
        @Override
        public Stream<Contributor> findContributorsByIds(Collection<UUID> ids) {
            return null;
        }

        @Override
        public Optional<Contributor> findById(UUID id) {
            return Optional.empty();
        }

        @Override
        public <S extends Contributor> S save(S entity) {
            return null;
        }

        @Override
        public Contributor getByInetAddress(InetAddress inetAddress) {
            calledInetAddress = inetAddress;
            return givenAnonymousContributor;
        }
    };

    ContributorApplicationService contributorApplicationService = new ContributorApplicationService(contributorRepository);
    InetAddress calledInetAddress;
    InetAddress givenInetAddress;
    AnonymousContributor givenAnonymousContributor;

    @BeforeEach
    void init() {
        calledInetAddress = null;
        givenInetAddress = aInetV4Address();
        givenAnonymousContributor = new AnonymousContributor(UUID.randomUUID(), givenInetAddress);
    }
    @Test
    void getContributorByIpAddress() {
        // given

        // when
        ContributorData when = contributorApplicationService.getContributorByIpAddress(givenInetAddress);

        // then
        assertThat(calledInetAddress)
                .isEqualTo(givenInetAddress);
        assertThat(when)
                .isNotNull()
                .isEqualTo(new ContributorData(givenAnonymousContributor.getId(), givenAnonymousContributor.getName(), ContributorType.ANON));
    }
}
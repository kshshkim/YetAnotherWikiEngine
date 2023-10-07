package dev.prvt.yawiki.core.contributor.application;

import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.contributor.domain.MemberContributor;
import dev.prvt.yawiki.core.event.MemberJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.prvt.yawiki.fixture.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

class ContributorMemberJoinEventHandlerTest {
    MemberContributor givenContributor;

    ContributorRepository contributorDummyRepository = new ContributorRepository() {
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
            createdContributor = entity;
            return entity;
        }

        @Override
        public Contributor getByInetAddress(InetAddress inetAddress) {
            return null;
        }
    };
    ContributorMemberJoinEventHandler contributorMemberJoinEventHandler = new ContributorMemberJoinEventHandler(contributorDummyRepository);
    MemberJoinEvent givenMemberJoinEvent;
    Contributor createdContributor;

    @BeforeEach
    void init() {
        givenMemberJoinEvent = new MemberJoinEvent(UUID.randomUUID(), randString());
        createdContributor = null;
    }

    @Test
    void handle() {
        // when
        contributorMemberJoinEventHandler.handle(givenMemberJoinEvent);

        // then
        assertThat(createdContributor.getId())
                .isEqualTo(givenMemberJoinEvent.memberId());
    }
}
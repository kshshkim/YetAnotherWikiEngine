package dev.prvt.yawiki.core.contributor.infra;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContributorRepositoryImpl implements ContributorRepository {
    private final ContributorJpaRepository contributorJpaRepository;
    private final UuidGenerator uuidGenerator;
    @Override
    public Stream<Contributor> findContributorsByIds(Collection<UUID> ids) {
        return contributorJpaRepository.findContributorsByIds(ids);
    }

    @Override
    public Optional<Contributor> findById(UUID id) {
        return contributorJpaRepository.findById(id);
    }

    @Override
    public <S extends Contributor> S save(S entity) {
        return contributorJpaRepository.save(entity);
    }

    private AnonymousContributor create(InetAddress inetAddress) {
        AnonymousContributor built = AnonymousContributor.builder()
                .id(uuidGenerator.generate())
                .ipAddress(inetAddress)
                .build();
        return contributorJpaRepository.save(built);
    }

    @Override
    public Contributor getByInetAddress(InetAddress inetAddress) {
        Optional<AnonymousContributor> found = contributorJpaRepository.findAnonymousContributorByIpAddress(inetAddress);
        return found.orElseGet(() -> create(inetAddress));
    }
}

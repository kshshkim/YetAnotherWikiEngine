package dev.prvt.yawiki.core.contributor.infra;

import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class ContributorRepositoryImpl implements ContributorRepository {
    private final ContributorJpaRepository contributorJpaRepository;
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
}

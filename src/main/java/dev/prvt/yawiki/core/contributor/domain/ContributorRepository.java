package dev.prvt.yawiki.core.contributor.domain;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ContributorRepository {
    Stream<Contributor> findContributorsByIds(Collection<UUID> ids);
    Optional<Contributor> findById(UUID id);
    <S extends Contributor> S save(S entity);
    Contributor getByInetAddress(InetAddress inetAddress);
}

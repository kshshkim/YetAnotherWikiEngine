package dev.prvt.yawiki.core.contributor.infra;

import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
import dev.prvt.yawiki.core.contributor.domain.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ContributorJpaRepository extends JpaRepository<Contributor, UUID> {
    @Query("select c from Contributor c where c.id in :ids")
    Stream<Contributor> findContributorsByIds(@Param("ids") Collection<UUID> ids);

    @Query("select ac from AnonymousContributor ac where ac.ipAddress = :ipAddress")
    Optional<AnonymousContributor> findAnonymousContributorByIpAddress(@Param("ipAddress") InetAddress inetAddress);
}

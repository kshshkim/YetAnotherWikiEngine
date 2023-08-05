package dev.prvt.yawiki.core.wikireference.domain;

import dev.prvt.yawiki.core.wikireference.infra.WikiReferenceCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WikiReferenceRepository extends JpaRepository<WikiReference, UUID>, WikiReferenceCustomRepository<UUID> {
}

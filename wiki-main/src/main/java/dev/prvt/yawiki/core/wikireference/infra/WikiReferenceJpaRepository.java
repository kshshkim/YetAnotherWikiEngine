package dev.prvt.yawiki.core.wikireference.infra;

import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WikiReferenceJpaRepository extends JpaRepository<WikiReference, UUID> {
}

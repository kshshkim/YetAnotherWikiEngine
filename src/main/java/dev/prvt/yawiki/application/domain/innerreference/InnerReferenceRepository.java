package dev.prvt.yawiki.application.domain.innerreference;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InnerReferenceRepository extends JpaRepository<InnerReference, UUID>, InnerReferenceCustomRepository<UUID> {
}

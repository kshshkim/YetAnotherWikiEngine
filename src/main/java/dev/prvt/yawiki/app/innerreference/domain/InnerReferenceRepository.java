package dev.prvt.yawiki.app.innerreference.domain;

import dev.prvt.yawiki.app.innerreference.infra.InnerReferenceCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InnerReferenceRepository extends JpaRepository<InnerReference, UUID>, InnerReferenceCustomRepository<UUID> {
}

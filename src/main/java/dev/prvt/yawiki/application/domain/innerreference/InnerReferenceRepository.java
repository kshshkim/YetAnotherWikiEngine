package dev.prvt.yawiki.application.domain.innerreference;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InnerReferenceRepository extends JpaRepository<InnerReference, Long>, InnerReferenceCustomRepository {
}

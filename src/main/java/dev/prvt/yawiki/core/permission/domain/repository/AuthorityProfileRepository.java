package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorityProfileRepository extends JpaRepository<AuthorityProfile, UUID> {
}

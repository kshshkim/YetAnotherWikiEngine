package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.ResourcePermission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {
    @EntityGraph(attributePaths = {"ownerGroup", "ownerGroup.defaultResourcePermission", "specificPermission"})
    Optional<ResourcePermission> findById(UUID uuid);
}

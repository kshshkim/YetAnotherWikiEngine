package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, UUID> {
}

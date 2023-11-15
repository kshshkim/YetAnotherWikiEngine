package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.model.NamespacePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NamespacePermissionRepository extends JpaRepository<NamespacePermission, Integer> {
}

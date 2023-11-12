package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.PagePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PagePermissionRepository extends JpaRepository<PagePermission, UUID> {
}

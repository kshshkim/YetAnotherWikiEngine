package dev.prvt.yawiki.core.permission.domain.repository;

import dev.prvt.yawiki.core.permission.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

}

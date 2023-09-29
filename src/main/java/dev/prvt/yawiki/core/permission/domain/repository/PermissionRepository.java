package dev.prvt.yawiki.core.permission.domain.repository;


import dev.prvt.yawiki.core.permission.domain.Permission;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission entity);

    Optional<Permission> findById(UUID uuid);

    Permission getOrCreateByAllAttributes(int c, int r, int u, int d, int m);
}

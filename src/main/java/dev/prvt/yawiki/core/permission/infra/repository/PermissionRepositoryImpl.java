package dev.prvt.yawiki.core.permission.infra.repository;

import dev.prvt.yawiki.core.permission.domain.Permission;
import dev.prvt.yawiki.core.permission.domain.PermissionData;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {
    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public Permission save(Permission entity) {
        return permissionJpaRepository.save(entity);
    }

    @Override
    public Optional<Permission> findById(UUID uuid) {
        return permissionJpaRepository.findById(uuid);
    }

    private Permission create(int c, int r, int u, int d, int m) {
        return permissionJpaRepository.save(
                Permission.builder()
                        .create(c)
                        .read(r)
                        .update(u)
                        .delete(d)
                        .manage(m)
                        .build()
        );
    }

    @Override
    public Permission getOrCreateByAllAttributes(int c, int r, int u, int d, int m) {
        Optional<Permission> found = permissionJpaRepository.findByAllAttributes(c, r, u, d, m);
        return found.orElseGet(() -> create(c, r, u, d, m));
    }

    @Override
    public Permission getOrCreateByAllAttributes(PermissionData permissionData) {
        return getOrCreateByAllAttributes(permissionData.getC(), permissionData.getR(), permissionData.getU(), permissionData.getD(), permissionData.getM());
    }
}

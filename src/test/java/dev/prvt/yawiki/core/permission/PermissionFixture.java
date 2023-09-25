package dev.prvt.yawiki.core.permission;

import dev.prvt.yawiki.core.permission.domain.Permission;

import java.util.UUID;

public class PermissionFixture {
    public static Permission.PermissionBuilder aPermission() {
        return Permission.builder()
                .id(UUID.randomUUID())
                .create(0)
                .read(1)
                .update(2)
                .delete(3)
                .manage(4);
    }
}

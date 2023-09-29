package dev.prvt.yawiki.core.permission;

import dev.prvt.yawiki.core.permission.domain.Permission;
import dev.prvt.yawiki.core.permission.domain.ResourcePermission;

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

    public static ResourcePermission.ResourcePermissionBuilder aResourcePermission() {
        return ResourcePermission.builder()
                .id(UUID.randomUUID());

    }
}

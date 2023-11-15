package dev.prvt.yawiki.fixture;

import dev.prvt.yawiki.core.permission.domain.model.GrantedPermission;
import dev.prvt.yawiki.core.permission.domain.model.NamespacePermission;
import dev.prvt.yawiki.core.permission.domain.model.Permission;

import static dev.prvt.yawiki.core.permission.domain.model.PermissionLevel.*;
import static dev.prvt.yawiki.fixture.Fixture.randString;

public class PermissionFixture {
    static public GrantedPermission.GrantedPermissionBuilder aGrantedPermission() {
        return GrantedPermission.builder()
                .comment(randString());
    }

    static public Permission.PermissionBuilder aPermission() {
        return Permission.builder()
                .create(EVERYONE)
                .editRequest(EVERYONE)
                .editCommit(EVERYONE)
                .rename(EVERYONE)
                .delete(EVERYONE)
                .discussionParticipate(EVERYONE)
                .discussionCreate(EVERYONE)
                .description(randString())
                ;
    }

    static public NamespacePermission.NamespacePermissionBuilder aNamespacePermission() {
        return NamespacePermission.builder()
                .downwardOverridable(true)
                .upwardOverridable(true);
    }
}

package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.PagePermission;
import dev.prvt.yawiki.core.permission.domain.model.Permission;

public interface PagePermissionUpdateValidator {
    void validate(PagePermission pagePermission, Permission permission);
}

package dev.prvt.yawiki.core.permission.domain;

public interface PagePermissionUpdateValidator {
    void validate(PagePermission pagePermission, Permission permission);
}

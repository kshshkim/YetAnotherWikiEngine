package dev.prvt.yawiki.core.permission.domain.impl;

import dev.prvt.yawiki.core.permission.domain.PagePermissionUpdateValidator;
import dev.prvt.yawiki.core.permission.domain.model.PagePermission;
import dev.prvt.yawiki.core.permission.domain.model.Permission;
import org.springframework.stereotype.Component;

@Component
public class PagePermissionUpdateValidatorImpl implements PagePermissionUpdateValidator {
    @Override
    public void validate(PagePermission pagePermission, Permission permission) {
        if (permission != null) {
            pagePermission.getNamespacePermission().validatePermissionOverride(permission);
        }
    }
}

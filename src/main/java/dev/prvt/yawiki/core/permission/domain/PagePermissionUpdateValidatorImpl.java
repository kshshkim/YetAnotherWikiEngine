package dev.prvt.yawiki.core.permission.domain;

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

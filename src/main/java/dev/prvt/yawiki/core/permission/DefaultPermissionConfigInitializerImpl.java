package dev.prvt.yawiki.core.permission;

import dev.prvt.yawiki.config.permission.DefaultPermissionConfigInitializer;
import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.permission.domain.model.NamespacePermission;
import dev.prvt.yawiki.core.permission.domain.model.Permission;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

import static dev.prvt.yawiki.core.permission.domain.model.PermissionLevel.*;

@Transactional
public class DefaultPermissionConfigInitializerImpl implements DefaultPermissionConfigInitializer {
    private final EntityManager em;
    private final DefaultPermissionProperties defaultPermissionProperties;

    public DefaultPermissionConfigInitializerImpl(EntityManager em, DefaultPermissionProperties defaultPermissionProperties) {
        this.em = em;
        this.defaultPermissionProperties = defaultPermissionProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (defaultPermissionProperties.isDoInitialize()) {
            initializePermissionConfig();
        }
    }

    private void initializePermissionConfig() {
        Stream.of(
                        nsNormal(),
                        nsFile(),
                        nsCategory(),
                        nsTemplate(),
                        nsMain()
                )
                .forEach(em::persist);
    }

    private Permission.PermissionBuilder basePermissionBuilder() {
        return Permission.builder()
                .create(EVERYONE)
                .editRequest(EVERYONE)
                .editCommit(EVERYONE)
                .rename(EVERYONE)
                .delete(EVERYONE)
                .discussionCreate(EVERYONE)
                .discussionParticipate(EVERYONE)
                ;
    }

    private Permission normalPermission() {
        return basePermissionBuilder()
                .description("normal")
                .build();
    }

    private Permission filePermission() {
        return basePermissionBuilder()
                .create(MEMBER)
                .editCommit(MEMBER)
                .delete(ASSISTANT_MANAGER)
                .rename(ASSISTANT_MANAGER)
                .delete(ASSISTANT_MANAGER)
                .description("file")
                .build();
    }

    private Permission templatePermission() {
        return basePermissionBuilder()
                .create(MEMBER)
                .editRequest(MEMBER)
                .editCommit(ASSISTANT_MANAGER)
                .delete(ASSISTANT_MANAGER)
                .rename(ASSISTANT_MANAGER)
                .description("template")
                .build();
    }

    private Permission categoryPermission() {
        return basePermissionBuilder()
                .create(MEMBER)
                .editRequest(MEMBER)
                .editCommit(ASSISTANT_MANAGER)
                .delete(ASSISTANT_MANAGER)
                .rename(ASSISTANT_MANAGER)
                .description("category")
                .build();
    }

    private Permission mainPermission() {
        return basePermissionBuilder()
                .create(MANAGER)
                .editRequest(MEMBER)
                .editCommit(MANAGER)
                .rename(MANAGER)
                .delete(MANAGER)
                .description("main")
                .build();
    }

    private NamespacePermission nsNormal() {
        return NamespacePermission.builder()
                .namespaceId(1)
                .upwardOverridable(true)
                .downwardOverridable(true)
                .permission(normalPermission())
                .build();
    }

    private NamespacePermission nsFile() {
        return NamespacePermission.builder()
                .namespaceId(3)
                .upwardOverridable(true)
                .downwardOverridable(false)
                .permission(filePermission())
                .build();
    }

    private NamespacePermission nsTemplate() {
        return NamespacePermission.builder()
                .namespaceId(5)
                .upwardOverridable(true)
                .downwardOverridable(false)
                .permission(templatePermission())
                .build();
    }

    private NamespacePermission nsCategory() {
        return NamespacePermission.builder()
                .namespaceId(7)
                .upwardOverridable(true)
                .downwardOverridable(false)
                .permission(categoryPermission())
                .build();
    }

    private NamespacePermission nsMain() {
        return NamespacePermission.builder()
                .namespaceId(9)
                .upwardOverridable(true)
                .downwardOverridable(false)
                .permission(mainPermission())
                .build();
    }




}

package dev.prvt.yawiki.config.permission;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.UUID;


@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "yawiki.default-permission")
public class DefaultPermissionProperties {
    private final int create;
    private final int read;
    private final int update;
    private final int delete;
    private final int manage;
    private final UUID defaultPermissionGroupId;

    public DefaultPermissionProperties(
            @DefaultValue("0") int create,
            @DefaultValue("0") int read,
            @DefaultValue("0") int update,
            @DefaultValue("0") int delete,
            @DefaultValue("4") int manage,
            @DefaultValue("00000000-0000-0000-0000-000000000001") UUID defaultPermissionGroupId
            ) {
        this.create = create;
        this.read = read;
        this.update = update;
        this.delete = delete;
        this.manage = manage;
        this.defaultPermissionGroupId = defaultPermissionGroupId;
    }
}

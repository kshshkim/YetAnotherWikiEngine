package dev.prvt.yawiki.config.permission;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;


@Getter
@ConfigurationProperties(prefix = "yawiki.default-permission")
public class DefaultPermissionProperties {
    private final boolean doInitialize;

    @ConstructorBinding
    public DefaultPermissionProperties(
            @DefaultValue("false") boolean doInitialize
            ) {
        this.doInitialize = doInitialize;
    }
}

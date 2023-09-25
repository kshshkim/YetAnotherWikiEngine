package dev.prvt.yawiki.config.permission;

import dev.prvt.yawiki.core.permission.domain.PermissionData;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(DefaultPermissionProperties.class)
@RequiredArgsConstructor
public class WikiPagePermissionConfig {
    private final DefaultPermissionProperties defaultPermissionProperties;

    @Bean("defaultPermission")
    public PermissionData defaultPermission() {
        return PermissionData.builder()
                .c(defaultPermissionProperties.getCreate())
                .r(defaultPermissionProperties.getRead())
                .u(defaultPermissionProperties.getUpdate())
                .d(defaultPermissionProperties.getDelete())
                .m(defaultPermissionProperties.getManage())
                .build();
    }
}

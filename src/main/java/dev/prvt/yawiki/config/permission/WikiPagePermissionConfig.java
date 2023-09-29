package dev.prvt.yawiki.config.permission;

import dev.prvt.yawiki.core.permission.DefaultPermissionConfigInitializerImpl;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;


@Configuration
@EnableConfigurationProperties(DefaultPermissionProperties.class)
@RequiredArgsConstructor
public class WikiPagePermissionConfig {
    private final DefaultPermissionProperties defaultPermissionProperties;
    private final EntityManager em;
    private final PermissionRepository permissionRepository;

    @Bean
    public DefaultPermissionConfigInitializer defaultPermissionConfigInitializer() {
        return new DefaultPermissionConfigInitializerImpl(em, permissionRepository, defaultPermissionProperties);
    }
}

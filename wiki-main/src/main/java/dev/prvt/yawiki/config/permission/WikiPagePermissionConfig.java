package dev.prvt.yawiki.config.permission;

import dev.prvt.yawiki.core.permission.DefaultPermissionConfigInitializerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;


@Configuration
@EnableConfigurationProperties(DefaultPermissionProperties.class)
@RequiredArgsConstructor
public class WikiPagePermissionConfig {
    private final DefaultPermissionProperties defaultPermissionProperties;
    private final EntityManager em;

    @Bean
    public DefaultPermissionConfigInitializer defaultPermissionConfigInitializer() {
        return new DefaultPermissionConfigInitializerImpl(em, defaultPermissionProperties);
    }
}

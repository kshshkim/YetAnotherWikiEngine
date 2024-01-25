package dev.prvt.yawiki.config.permission;

import dev.prvt.yawiki.core.permission.domain.AuthorityLevelFinder;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import dev.prvt.yawiki.core.permission.infra.RepositoryAuthorityLevelFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AuthorityLevelFinderConfig {
    private final AuthorityProfileRepository authorityProfileRepository;

    @Bean
    public AuthorityLevelFinder authorityLevelFinder() {
        return new RepositoryAuthorityLevelFinder(authorityProfileRepository);
    }
}

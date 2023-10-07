package dev.prvt.yawiki.config.springsecurity;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * 개발 과정에서 임시로 추가한 설정값임. 추후 커스텀 가능하도록 수정할 예정임.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer())
                .sessionManagement(sessionManagementCustomizer())
                .csrf(csrfCustomizer())
                .cors(conf -> conf.configure(httpSecurity))
                .oauth2ResourceServer(auth2ResourceServerCustomizer())
        ;

        return httpSecurity.build();
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");

        return corsConfiguration;
    }

    @NotNull
    private static Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> auth2ResourceServerCustomizer() {
        return configurer -> configurer.jwt(withDefaults());
    }

    @NotNull
    private static Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @NotNull
    private static Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementCustomizer() {
        return session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @NotNull
    private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer() {
        return auth -> {
            auth.anyRequest().permitAll();
        };
    }
}

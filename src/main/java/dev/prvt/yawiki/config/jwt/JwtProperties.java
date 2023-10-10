package dev.prvt.yawiki.config.jwt;

import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "yawiki.jwt")
public class JwtProperties {
    private final int lifespan;
    private final int refreshTokenLifespan;
    private final String issuer;
    private final String subject;

    @Builder
    public JwtProperties(
            @DefaultValue("1800") int lifespan,
            @DefaultValue("180000") int refreshTokenLifespan,
            @DefaultValue("self") String issuer,
            @DefaultValue("yawiki") String subject
            ) {
        this.lifespan = lifespan;
        this.refreshTokenLifespan = refreshTokenLifespan;
        this.issuer = issuer;
        this.subject = subject;
    }
}

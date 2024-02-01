package dev.prvt.yawiki.member.config;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.common.uuid.UuidGenerators;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UuidConfiguration {

    @Bean
    public UuidGenerator uuidGenerator() {
        return UuidGenerators.uuidV7Generator();
    }

}

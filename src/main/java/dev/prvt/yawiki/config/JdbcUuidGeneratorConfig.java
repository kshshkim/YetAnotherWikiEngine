package dev.prvt.yawiki.config;

import com.fasterxml.uuid.Generators;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.common.uuid.FasterXmlNoArgGeneratorAdapter;
import dev.prvt.yawiki.common.uuid.UuidV7Generator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcUuidGeneratorConfig {
    /**
     * <p>일부 JDBC 쿼리에 사용되는 UUID 생성기. Clustered Index 와 함께 사용해도 문제가 없는 UUID V7 을 생성함.</p>
     * <p>JPA 엔티티의 ID 생성 전략과는 관계가 없음. JPA ID 생성 전략과 관련해선 {@link UuidV7Generator} 참조.</p>
     * @return UUIDGenerator
     */
    @Bean
    public UuidGenerator uuidGenerator() {
        return new FasterXmlNoArgGeneratorAdapter(Generators.timeBasedEpochGenerator());
    }
}

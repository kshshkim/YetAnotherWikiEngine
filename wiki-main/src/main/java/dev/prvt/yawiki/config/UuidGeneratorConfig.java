package dev.prvt.yawiki.config;

import com.fasterxml.uuid.Generators;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.common.uuid.FasterXmlNoArgGeneratorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UuidGeneratorConfig {

    /**
     * <p>Clustered Index 와 함께 사용해도 문제가 없는 UUID V7 을 생성함.</p>
     * <p><b>JPA 엔티티의 ID 생성기 전략과는 관계가 없음.</b>(JPA ID Generator는 스프링 Configuration으로 지정할 수 없음.)</p>
     * @return UUIDGenerator
     */
    @Bean
    public UuidGenerator uuidGenerator() {
        return new FasterXmlNoArgGeneratorAdapter(Generators.timeBasedEpochGenerator());
    }

}

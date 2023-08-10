package dev.prvt.yawiki.common.uuid;

import com.fasterxml.uuid.NoArgGenerator;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * {@link NoArgGenerator} 구현체 어댑터
 */
@RequiredArgsConstructor
public class FasterXmlNoArgGeneratorAdapter implements UuidGenerator {
    private final NoArgGenerator generator;
    @Override
    public UUID generate() {
        return generator.generate();
    }
}

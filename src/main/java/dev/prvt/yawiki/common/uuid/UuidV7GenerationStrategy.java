package dev.prvt.yawiki.common.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerationStrategy;

import java.util.UUID;

@Slf4j
public class UuidV7GenerationStrategy implements UUIDGenerationStrategy {
    static public final UuidV7GenerationStrategy INSTANCE = new UuidV7GenerationStrategy();
    private final NoArgGenerator uuidV7Generator = Generators.timeBasedEpochGenerator();
    @Override
    public int getGeneratedVersion() {
        return 7;
    }

    @Override
    public UUID generateUUID(SharedSessionContractImplementor session) {
        UUID generated = uuidV7Generator.generate();
        log.trace("generated! uuid={}", generated);
        return generated;
    }
}

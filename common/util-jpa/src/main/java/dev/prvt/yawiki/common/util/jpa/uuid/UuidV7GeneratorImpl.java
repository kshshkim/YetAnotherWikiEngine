package dev.prvt.yawiki.common.util.jpa.uuid;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.common.uuid.UuidGenerators;
import java.util.EnumSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.type.descriptor.java.UUIDJavaType;
import org.hibernate.type.descriptor.java.UUIDJavaType.ValueTransformer;


/**
 * <p>UUIDv7을 생성하는 커스텀 JPA UuidGenerator 구현체. 원본({@link org.hibernate.id.uuid.UuidGenerator})과 달리, 변환 타입으로 {@link java.util.UUID}만 지원함.</p>
 * @see org.hibernate.id.uuid.UuidGenerator
 */
public class UuidV7GeneratorImpl implements BeforeExecutionGenerator {

    private final UuidGenerator uuidGenerator;
    private final ValueTransformer valueTransformer;

    public UuidV7GeneratorImpl(
    ) {
        uuidGenerator = UuidGenerators.uuidV7Generator();
        valueTransformer = UUIDJavaType.PassThroughTransformer.INSTANCE;
    }

    @Override
    public Object generate(
        SharedSessionContractImplementor session,
        Object owner,
        Object currentValue,
        EventType eventType
    ) {
        return valueTransformer.transform(uuidGenerator.generate());
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return INSERT_ONLY;
    }
}

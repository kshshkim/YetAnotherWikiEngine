package dev.prvt.yawiki.common.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import java.util.Random;

public class UuidGenerators {

    /**
     * {@link TimeBasedEpochGenerator} 는 내부적으로 generate 메서드 단위에 락을 사용하며,
     * {@link Random} 인자를 넘기지 않고 생성할 경우 {@link java.security.SecureRandom} 싱글톤 인스턴스를 사용함.
     * 동시 성능이 문제가 되는 경우 인스턴스를 참조하지 않아야하며, 싱글톤 패턴으로 사용하기보단 적절히 새로운 인스턴스를 생성하여야함.
     */
    public static final UuidGenerator UUID_V7_INSTANCE
        = new FasterXmlNoArgGeneratorAdapter(Generators.timeBasedEpochGenerator());

    public static UuidGenerator uuidV7Generator() {
        return new FasterXmlNoArgGeneratorAdapter(Generators.timeBasedEpochGenerator());
    }

    public static UuidGenerator uuidV7Generator(Random random) {
        return new FasterXmlNoArgGeneratorAdapter(new TimeBasedEpochGenerator(random));
    }

}

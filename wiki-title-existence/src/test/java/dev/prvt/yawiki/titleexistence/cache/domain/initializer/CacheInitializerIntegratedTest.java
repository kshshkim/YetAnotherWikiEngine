package dev.prvt.yawiki.titleexistence.cache.domain.initializer;

import dev.prvt.yawiki.titleexistence.cache.infra.CacheStorageConcurrentHashMapImpl;
import dev.prvt.yawiki.titleexistence.cache.infra.initializer.InitialCacheDataReaderJdbcImpl;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CacheInitializerIntegratedTest {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    CacheInitializer cacheInitializer;
    CacheStorageConcurrentHashMapImpl cacheStorage;

    @BeforeEach
    void init() {
        cacheStorage = new CacheStorageConcurrentHashMapImpl();

        cacheInitializer = new CacheInitializer(
            new InitialCacheDataReaderJdbcImpl(namedParameterJdbcTemplate, 30),
            cacheStorage);
    }

    @Test
    @Disabled
    @DisplayName("초기화시 사용되는 메모리와 소요되는 시간을 확인하고자 작성한 테스트. TestContainer 환경에서는 의미가 없기 때문에 비활성화함.")
    void test() {
        long startedAt = System.currentTimeMillis();
        cacheInitializer.initialize(LocalDateTime.now());
        long finishedAt = System.currentTimeMillis();

        System.out.println("finished in " + (finishedAt - startedAt) + "ms");
        System.out.println();
    }

}

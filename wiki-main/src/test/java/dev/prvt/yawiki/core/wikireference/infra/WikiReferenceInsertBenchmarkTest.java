package dev.prvt.yawiki.core.wikireference.infra;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.uuid.UuidGenerator;
import dev.prvt.yawiki.common.uuid.UuidGenerators;
import dev.prvt.yawiki.common.uuid.UuidUtil;
import dev.prvt.yawiki.core.wikireference.domain.WikiReference;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>Bulk Insert 시, rewriteBatchedStatements 옵션이 사용되는 경우 Jdbc 쿼리가 10배 가량 빠름.</p>
 * <p>rewriteBatchedStatements 옵션이 사용되지 않는 경우 큰 차이가 없음.</p>
 * <p>여러 개의 insert 쿼리를 하나의 쿼리로 합쳐주는 옵션인데, 무시무시한 차이를 보이니 반드시 사용할것.</p>
 */
@Slf4j
@Disabled
@SpringBootTest
public class WikiReferenceInsertBenchmarkTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    WikiReferenceJpaRepository wikiReferenceJpaRepository;

    int TITLE_COUNT = 1000;

    private List<String> generateTitles(int howMany) {
        return IntStream.range(0, howMany)
                .mapToObj(i -> randString())
                .toList();
    }


    @Disabled
    @RepeatedTest(1000)
    @Transactional
    @Commit
    void jdbcBulkInsert() {
        List<String> titles = generateTitles(TITLE_COUNT);
        UuidGenerator uuidGenerator = UuidGenerators.uuidV7Generator();

        UUID documentId = UUID.randomUUID();
        byte[] refererId = UuidUtil.asByteArray(documentId);

        String sql = "insert into wiki_reference (referer_id, referred_title, referred_namespace) values (?, ?, ?)";

        long startTime = System.currentTimeMillis();
        jdbcTemplate.batchUpdate(sql,
                titles,
                titles.size(),
                (PreparedStatement ps, String title) -> {
//                    ps.setBytes(1, UUIDUtil.asByteArray(generator.generate()));
                    ps.setBytes(1, refererId);
                    ps.setString(2, title);
                    ps.setInt(3, 1);
                }
        );
        long endTime = System.currentTimeMillis();
        log.info("finished in {}ms", endTime - startTime);
    }

    @Disabled
    @RepeatedTest(100)
    @Transactional
    void dataJpaBulkInsert() {
        List<String> titles = generateTitles(TITLE_COUNT);
        UUID documentId = UUID.randomUUID();

        long mappingStartTime = System.currentTimeMillis();
        List<WikiReference> list = titles.stream().map(title -> new WikiReference(documentId, title, Namespace.NORMAL))
                .toList();

        long queryStartTime = System.currentTimeMillis();
        wikiReferenceJpaRepository.saveAllAndFlush(list);
        long endTime = System.currentTimeMillis();
        log.info("total job finished in {}ms", endTime - mappingStartTime);
        log.info("query finished in {}ms", endTime - queryStartTime);
    }
}

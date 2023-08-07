package dev.prvt.yawiki.core.wikipage.domain.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WikiPageRepository extends JpaRepository<WikiPage, UUID> {
    Optional<WikiPage> findByTitle(String title);

    @Query("select wp from WikiPage wp join fetch wp.currentRevision cr join fetch cr.rawContent where wp.title = :title")
    Optional<WikiPage> findByTitleWithRevisionAndRawContent(@Param("title") String title);
}

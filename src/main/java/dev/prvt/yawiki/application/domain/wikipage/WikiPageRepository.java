package dev.prvt.yawiki.application.domain.wikipage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WikiPageRepository extends JpaRepository<WikiPage, UUID> {
    Optional<WikiPage> findByTitle(String title);

    @Query("select d.editToken from WikiPage d where d.title = :title")
    String findEditTokenByTitle(@Param("title") String title);

    @Query("select wp from WikiPage wp join fetch wp.currentRevision cr join fetch cr.rawContent")
    Optional<WikiPage> findByTitleWithRevisionAndRawContent(String title);
}

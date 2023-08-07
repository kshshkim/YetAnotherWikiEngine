package dev.prvt.yawiki.core.wikipage.infra.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WikiPageRepositoryImpl implements WikiPageRepository {
    private final WikiPageJpaRepository wikiPageJpaRepository;

    public WikiPageRepositoryImpl(WikiPageJpaRepository wikiPageJpaRepository, EntityManager entityManager) {
        this.wikiPageJpaRepository = wikiPageJpaRepository;
    }

    @Override
    public WikiPage findOrCreate(String title) {
        return wikiPageJpaRepository.findByTitle(title)
                .orElseGet(() -> wikiPageJpaRepository.save(WikiPage.create(title)));
    }

    @Override
    public Optional<WikiPage> findById(UUID id) {
        return wikiPageJpaRepository.findById(id);
    }

    @Override
    public WikiPage save(WikiPage entity) {
        return wikiPageJpaRepository.save(entity);
    }

    @Override
    public Optional<WikiPage> findByTitle(String title) {
        return wikiPageJpaRepository.findByTitle(title);
    }

    @Override
    public Optional<WikiPage> findByTitleWithRevisionAndRawContent(String title) {
        return wikiPageJpaRepository.findByTitleWithRevisionAndRawContent(title);
    }
}

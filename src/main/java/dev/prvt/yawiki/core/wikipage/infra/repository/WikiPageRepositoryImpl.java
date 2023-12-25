package dev.prvt.yawiki.core.wikipage.infra.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class WikiPageRepositoryImpl implements WikiPageRepository {
    private final WikiPageJpaRepository wikiPageJpaRepository;

    public WikiPageRepositoryImpl(WikiPageJpaRepository wikiPageJpaRepository) {
        this.wikiPageJpaRepository = wikiPageJpaRepository;
    }

    @Override
    public Optional<WikiPage> findById(UUID id) {
        return wikiPageJpaRepository.findById(id);
    }

    @Override
    @Transactional
    public WikiPage save(WikiPage entity) {
        return wikiPageJpaRepository.save(entity);
    }

    @Override
    public Optional<WikiPage> findByTitleAndNamespace(String title, Namespace namespace) {
        return wikiPageJpaRepository.findByTitleWithRevisionAndRawContent(title, namespace);
    }
}

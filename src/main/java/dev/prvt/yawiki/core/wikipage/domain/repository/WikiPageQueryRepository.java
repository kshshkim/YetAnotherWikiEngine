package dev.prvt.yawiki.core.wikipage.domain.repository;


import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WikiPageQueryRepository {
    Page<Revision> findRevisionsByTitle(String title, Pageable pageable);
}

package dev.prvt.yawiki.core.wikititle.history.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TitleHistoryRepository extends JpaRepository<TitleHistory, UUID> {
}

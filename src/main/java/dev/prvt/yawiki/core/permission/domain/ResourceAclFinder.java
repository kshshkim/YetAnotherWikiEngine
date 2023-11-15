package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.YawikiPermission;

import java.util.Optional;
import java.util.UUID;

/**
 * 자원의 접근 권한을 찾아 반환함.
 */
public interface ResourceAclFinder {
    /**
     * 위키 페이지의 ID 를 인자로 받아 해당 위키 페이지의 접근 권한을 반환함.
     * @param wikiPageId 위키 페이지의 ID
     * @return 해당 위키 페이지의 ACL
     */
    Optional<YawikiPermission> findWikiPageAclByWikiPageId(UUID wikiPageId);
}

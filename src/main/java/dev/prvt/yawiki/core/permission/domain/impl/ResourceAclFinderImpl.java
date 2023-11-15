package dev.prvt.yawiki.core.permission.domain.impl;

import dev.prvt.yawiki.core.permission.domain.model.PagePermission;
import dev.prvt.yawiki.core.permission.domain.ResourceAclFinder;
import dev.prvt.yawiki.core.permission.domain.model.YawikiPermission;
import dev.prvt.yawiki.core.permission.domain.repository.PagePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResourceAclFinderImpl implements ResourceAclFinder {
    private final PagePermissionRepository pagePermissionRepository;
    @Override
    public Optional<YawikiPermission> findWikiPageAclByWikiPageId(UUID wikiPageId) {
        Optional<PagePermission> found = pagePermissionRepository.findById(wikiPageId);
        return found.map(pagePermission -> (YawikiPermission) pagePermission);
    }
}

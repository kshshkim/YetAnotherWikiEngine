package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.AuthorityLevelFinder;
import dev.prvt.yawiki.core.permission.domain.model.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;

import java.util.Optional;
import java.util.UUID;


/**
 * 매번 리포지토리에 접근하여 권한 정보를 가져오는 AuthorityLevelFinder 구현체.
 */
public class RepositoryAuthorityLevelFinder implements AuthorityLevelFinder {
    public RepositoryAuthorityLevelFinder(AuthorityProfileRepository authorityProfileRepository) {
        this.authorityProfileRepository = authorityProfileRepository;
    }
    private final AuthorityProfileRepository authorityProfileRepository;

    @Override
    public PermissionLevel findPermissionLevelByActorId(UUID actorId) {
        Optional<AuthorityProfile> found = authorityProfileRepository.findById(actorId);
        if (found.isEmpty()) {  // 비로그인 사용자는 AuthorityProfile 엔티티를 가지고 있지 않음.
            return PermissionLevel.EVERYONE;
        }
        return found.get().getMaxPermissionLevel(0L);  // 가장 높은 권한을 반환함.
    }
}

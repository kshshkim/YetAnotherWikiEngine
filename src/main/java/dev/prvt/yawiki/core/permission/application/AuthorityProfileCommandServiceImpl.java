package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.AuthorityGrantValidator;
import dev.prvt.yawiki.core.permission.domain.AuthorityProfile;
import dev.prvt.yawiki.core.permission.domain.NoSuchAuthorityProfileException;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorityProfileCommandServiceImpl implements AuthorityProfileCommandService {
    private final AuthorityProfileRepository authorityProfileRepository;
    private final AuthorityGrantValidator authorityGrantValidator;

    @Override
    public void createAuthorityProfile(UUID contributorId) {
        authorityProfileRepository.save(AuthorityProfile.create(contributorId));
    }

    @Override
    public void grantAuthority(AuthorityGrantData authorityGrantData) {
        AuthorityProfile granter = authorityProfileRepository.findById(authorityGrantData.granterId())
        // Granter 프로필이 존재하지 않는 상황은 외부로 나가서는 안 될 시스템의 문제이기 때문에 IllegalStateException 반환.
                .orElseThrow(() -> new IllegalStateException("Granter 가 존재하지 않음. ID: " + authorityGrantData.granterId()));

        AuthorityProfile grantee = authorityProfileRepository.findById(authorityGrantData.granteeId())
        // Grantee 프로필이 존재하지 않는 상황은 API 요청자에게 알려줘야함. 커스텀 예외 처리.
                .orElseThrow(() -> new NoSuchAuthorityProfileException(authorityGrantData.granteeId()));

        granter.grantPermissionTo(
                grantee,
                authorityGrantData.permissionLevel(),
                authorityGrantData.comment(),
                authorityGrantData.expiresAt(),
                authorityGrantValidator
        );
    }
}

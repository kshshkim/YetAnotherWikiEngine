package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPagePermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * db 에 저장된 권한을 쿼리해서 편집 권한이 있는지 여부를 판별함.
 * 스프링 시큐리티의 시큐리티 컨텍스트에 접근해서 권한을 가져올 수도 있을 것으로 보임.
 *
 */
@Component
@RequiredArgsConstructor
public class WikiPagePermissionValidatorImpl implements WikiPagePermissionValidator {


    @Override
    public void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException {

    }

    @Override
    public void validateDelete(UUID actorId, WikiPage wikiPage) {

    }

    @Override
    public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {

    }
}

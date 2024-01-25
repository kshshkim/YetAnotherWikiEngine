package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.model.Permission;
import dev.prvt.yawiki.core.permission.domain.model.PermissionData;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public Permission map(PermissionData from) {
        return Permission.builder()
                .description(from.description())
                .create(from.create())
                .editCommit(from.editCommit())
                .editRequest(from.editRequest())
                .delete(from.delete())
                .discussionCreate(from.discussionCreate())
                .discussionParticipate(from.discussionParticipate())
                .rename(from.rename())
                .build();
    }
}

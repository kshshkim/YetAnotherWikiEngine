package dev.prvt.yawiki.core.permission.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * <p>Permission 세부 정보만 따로 분리하여 캐싱할 수 있음. 캐싱 편의를 위해 immutable 하게 설정함.</p>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "perm_permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perm_id")
    private Integer id;
    @Column(nullable = false)
    private String description;
    @Column(name = "create_new", columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel create;
    @Column(name = "edit_commit", columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel editCommit;
    @Column(name = "edit_request", columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel editRequest;
    @Column(name = "delete_commit",columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel delete;
    @Column(name = "rename_title", columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel rename;
    @Column(name = "discussion_create",columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel discussionCreate;
    @Column(name = "discussion_participate", columnDefinition = "TINYINT(3)", updatable = false)
    private PermissionLevel discussionParticipate;

    private void validateDescription(String description) {
        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("description cannot be empty or null");
        }
    }

    public PermissionLevel getPermissionLevel(ActionType actionType) {
        return switch (actionType) {
            case CREATE -> this.create;
            case EDIT_COMMIT -> this.editCommit;
            case EDIT_REQUEST -> this.editRequest;
            case DELETE -> this.delete;
            case RENAME -> this.rename;
            case DISCUSSION_CREATE -> this.discussionCreate;
            case DISCUSSION_PARTICIPATE -> this.discussionParticipate;
        };
    }

    @Builder
    protected Permission(Integer id, String description, PermissionLevel create, PermissionLevel editCommit, PermissionLevel editRequest, PermissionLevel delete, PermissionLevel rename, PermissionLevel discussionCreate, PermissionLevel discussionParticipate) {
        validateDescription(description);
        this.id = id;
        this.description = description;
        this.create = create;
        this.editCommit = editCommit;
        this.editRequest = editRequest;
        this.delete = delete;
        this.rename = rename;
        this.discussionCreate = discussionCreate;
        this.discussionParticipate = discussionParticipate;
    }
}

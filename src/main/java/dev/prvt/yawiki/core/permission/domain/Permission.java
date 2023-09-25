package dev.prvt.yawiki.core.permission.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static dev.prvt.yawiki.common.uuid.Const.UUID_V7;

/**
 * <h2>Permission</h2>
 * <p>Permission 상세 값을 가지는 Immutable 엔티티. 값을 수정할 수 없으며, 고유한 값의 Acl 은 단 하나만 존재해야함.</p>
 * <p>각 권한은 0~4 사이의 값을 가짐. c, r, u, d, manage 는 각각 create, read, update, delete, manage 에 해당됨.</p>
 * <p>Permission 을 다른 Permission 으로 교체하기 위해서는 수행자의 권한이 manage 보다 높아야하며, 수행자의 권한보다 높은 값을 할당해선 안 됨.</p>
 * <ul> 권한 단계
 *      <li>0: everyone</li>
 *      <li>1: group</li>
 *      <li>2: group manager</li>
 *      <li>3: group admin</li>
 *      <li>4: system admin</li>
 * </ul>
 */
@Getter
@Entity
@Table(
        name = "permissions",
        indexes = {
                @Index(
                        name = "idx__unique_all_fields",
                        columnList = "c, r, u, d, m",
                        unique = true
                )
        }
)
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(generator = "uuid-v7")
    @GenericGenerator(name = "uuid-v7", strategy = UUID_V7)
    @Column(name = "permission_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "c")
    private Integer create;
    @Column(name = "r")
    private Integer read;
    @Column(name = "u")
    private Integer update;
    @Column(name = "d")
    private Integer delete;
    @Column(name = "m")
    private Integer manage;

    @Transient
    public Integer getRequiredLevel(ActionType actionType) {
        return switch (actionType) {
            case CREATE -> create;
            case READ -> read;
            case UPDATE -> update;
            case DELETE -> delete;
            case MANAGE -> manage;
        };
    }

    @Builder
    protected Permission(UUID id, Integer create, Integer read, Integer update, Integer delete, Integer manage) {
        this.id = id;
        this.create = create;
        this.read = read;
        this.update = update;
        this.delete = delete;
        this.manage = manage;
    }
}

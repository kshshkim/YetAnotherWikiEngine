package dev.prvt.yawiki.core.permission.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PermissionData {
    private final int c;
    private final int r;
    private final int u;
    private final int d;
    private final int m;

    @Builder
    protected PermissionData(int c, int r, int u, int d, int m) {
        this.c = c;
        this.r = r;
        this.u = u;
        this.d = d;
        this.m = m;
    }

    static public PermissionData from(Permission permission) {
        return PermissionData.builder()
                .c(permission.getCreate())
                .r(permission.getRead())
                .u(permission.getUpdate())
                .d(permission.getDelete())
                .m(permission.getManage())
                .build();
    }
}

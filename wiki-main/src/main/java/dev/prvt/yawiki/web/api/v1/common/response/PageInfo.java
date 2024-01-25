package dev.prvt.yawiki.web.api.v1.common.response;

import org.springframework.data.domain.Page;

public record PageInfo(
        int count,
        int pageSize,
        int pageNumber,
        int totalPages
) {
    public static PageInfo from(Page<?> page) {
        return new PageInfo(page.getNumberOfElements(), page.getSize(), page.getNumber(), page.getTotalPages());
    }
}

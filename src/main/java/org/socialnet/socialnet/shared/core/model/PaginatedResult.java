package org.socialnet.socialnet.shared.core.model;

import java.util.List;

public record PaginatedResult<T> (
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<T> content) {
}

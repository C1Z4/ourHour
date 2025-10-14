package com.ourhour.global.util;

import java.util.List;
import java.util.function.Function;

import com.ourhour.global.common.dto.PageResponse;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static <T, R> PageResponse<R> paginate(List<T> allItems, int currentPage, int size, Function<T, R> mapper) {
        // null 또는 빈 리스트 처리
        if (allItems == null || allItems.isEmpty()) {
            return PageResponse.empty(Math.max(1, currentPage), Math.max(1, size));
        }

        // size가 0 이하인 경우 처리
        if (size <= 0) {
            return PageResponse.empty(Math.max(1, currentPage), 1);
        }

        int total = allItems.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int start = Math.max(0, (currentPage - 1) * size);
        int end = Math.min(start + size, total);
        List<R> data = allItems.subList(start, end).stream().map(mapper).toList();
        return PageResponse.<R>builder()
                .data(data)
                .currentPage(currentPage)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(currentPage < totalPages)
                .hasPrevious(currentPage > 1)
                .build();
    }
}

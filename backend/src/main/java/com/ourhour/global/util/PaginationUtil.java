package com.ourhour.global.util;

import java.util.List;
import java.util.function.Function;

import com.ourhour.global.common.dto.PageResponse;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static <T, R> PageResponse<R> paginate(List<T> allItems, int currentPage, int size, Function<T, R> mapper) {
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

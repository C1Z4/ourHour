package com.backend.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    private List<T> data;        // 실제 데이터
    private int currentPage;        // 현재 페이지
    private int size;              // 페이지당 데이터 갯수
    private int totalPages;        // 총 페이지 갯수
    private long totalElements;    // 총 데이터 갯수
    private boolean hasNext;       // 다음페이지 존재여부
    private boolean hasPrevious;   // 이전페이지 존재여부
    
    // Page 객체로부터 PageResponse 생성하는 정적 팩토리 메서드
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .data(page.getContent())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
    
    // 빈 페이지 생성
    public static <T> PageResponse<T> empty(int currentPage, int size) {
        return PageResponse.<T>builder()
                .data(List.of())
                .currentPage(currentPage)
                .size(size)
                .totalPages(0)
                .totalElements(0)
                .hasNext(false)
                .hasPrevious(currentPage > 0)
                .build();
    }
} 
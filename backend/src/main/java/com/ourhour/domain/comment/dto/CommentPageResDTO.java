package com.ourhour.domain.comment.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentPageResDTO {
    private Long postId;
    private Long issueId;
    private List<CommentDTO> comments;

    private int currentPage;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;

    public static CommentPageResDTO of(CommentResDTO commentResDTO, int currentPage, int size,
            int totalPages, long totalElements, boolean hasNext, boolean hasPrevious) {
        return CommentPageResDTO.builder()
                .postId(commentResDTO.getPostId())
                .issueId(commentResDTO.getIssueId())
                .comments(commentResDTO.getComments())
                .currentPage(currentPage)
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }

    public static CommentPageResDTO empty(Long postId, Long issueId, int currentPage, int size) {
        return CommentPageResDTO.builder()
                .postId(postId)
                .issueId(issueId)
                .comments(List.of())
                .currentPage(currentPage)
                .size(size)
                .totalPages(0)
                .totalElements(0)
                .hasNext(false)
                .hasPrevious(currentPage > 1)
                .build();
    }
}
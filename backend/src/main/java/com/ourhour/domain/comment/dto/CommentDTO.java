package com.ourhour.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long commentId;
    private Long authorId;
    private String name;
    private String profileImg;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentDTO> childComments;
}

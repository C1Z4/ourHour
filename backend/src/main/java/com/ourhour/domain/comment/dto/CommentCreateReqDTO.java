package com.ourhour.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateReqDTO {
    private Long postId;
    private Long issueId;
    private Long authorId;
    private Long parentCommentId;
    private String content;
}

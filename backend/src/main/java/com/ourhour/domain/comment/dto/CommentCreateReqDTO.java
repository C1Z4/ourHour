package com.ourhour.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateReqDTO {
    private Long postId;
    private Long issueId;
    private Long parentCommentId;
    private String content;
}

package com.ourhour.domain.comment.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResDTO {
    private Long postId;
    private Long issueId;
    private List<CommentDTO> comments;
}

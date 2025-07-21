package com.ourhour.domain.board.exceptions;

import com.ourhour.global.exception.BusinessException;

public class PostException extends BusinessException {

    public PostException(Long postId) {
        // HTTP 404 상태 코드와 함께, postId를 포함한 명확한 메시지를 전달
        super(404, "게시글을 찾을 수 없습니다. ID: " + postId);
    }
}
package com.ourhour.domain.board.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class PostException extends BusinessException {

    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PostException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static PostException postNotFoundException() {
        return new PostException(ErrorCode.POST_NOT_FOUND);
    }

    public static PostException postAccessDeniedException() {
        return new PostException(ErrorCode.POST_ACCESS_DENIED);
    }

    public static PostException postAuthorNotFoundException() {
        return new PostException(ErrorCode.POST_AUTHOR_NOT_FOUND);
    }

    public static PostException postAuthorAccessDeniedException() {
        return new PostException(ErrorCode.POST_AUTHOR_ACCESS_DENIED);
    }

    public static PostException postAuthorDeleteAccessDeniedException() {
        return new PostException(ErrorCode.POST_AUTHOR_DELETE_ACCESS_DENIED);
    }

    public static PostException postUpdateAccessDeniedException() {
        return new PostException(ErrorCode.POST_UPDATE_ACCESS_DENIED);
    }
}

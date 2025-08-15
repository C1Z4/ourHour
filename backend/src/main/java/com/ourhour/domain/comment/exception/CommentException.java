package com.ourhour.domain.comment.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class CommentException extends BusinessException {

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommentException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static CommentException commentNotFoundException() {
        return new CommentException(ErrorCode.COMMENT_NOT_FOUND);
    }

    public static CommentException commentContentRequiredException() {
        return new CommentException(ErrorCode.COMMENT_CONTENT_REQUIRED);
    }

    public static CommentException commentContentTooLongException() {
        return new CommentException(ErrorCode.COMMENT_CONTENT_TOO_LONG);
    }

    public static CommentException commentAuthorRequiredException() {
        return new CommentException(ErrorCode.COMMENT_AUTHOR_REQUIRED);
    }

    public static CommentException commentTargetRequiredException() {
        return new CommentException(ErrorCode.COMMENT_TARGET_REQUIRED);
    }

    public static CommentException commentTargetConflictException() {
        return new CommentException(ErrorCode.COMMENT_TARGET_CONFLICT);
    }

    public static CommentException commentAlreadyLikedException() {
        return new CommentException(ErrorCode.COMMENT_ALREADY_LIKED);
    }

    public static CommentException commentLikeNotFoundException() {
        return new CommentException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
    }
}
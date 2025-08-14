package com.ourhour.domain.board.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class BoardException extends BusinessException {

    public BoardException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BoardException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static BoardException boardNotFoundException() {
        return new BoardException(ErrorCode.BOARD_NOT_FOUND);
    }

    public static BoardException boardAuthorNotFoundException() {
        return new BoardException(ErrorCode.BOARD_AUTHOR_NOT_FOUND);
    }
}
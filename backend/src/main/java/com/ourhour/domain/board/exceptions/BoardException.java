package com.ourhour.domain.board.exceptions;

import com.ourhour.global.exception.BusinessException;

public class BoardException extends BusinessException {
    public BoardException(int status, String message) {
        super(status, message);
    }

    public static BoardException authorNotFoundException() {
        return new BoardException(404, "해당 작성자를 찾을 수 없습니다.");
    }
}

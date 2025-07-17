package com.ourhour.domain.member.exceptions;

import com.ourhour.global.exception.BusinessException;

public class MemberException extends BusinessException {

    public MemberException(int status, String message) {
        super(status, message);
    }

    public static MemberException memberNotFoundException() {
        return new MemberException(404, "해당 멤버를 찾을 수 없습니다.");
    }
}

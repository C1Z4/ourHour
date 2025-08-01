package com.ourhour.domain.member.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class MemberException extends BusinessException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static MemberException memberNotFoundException() {
        return new MemberException(ErrorCode.MEMBER_NOT_FOUND);
    }

    public static MemberException memberAccessDeniedException() {
        return new MemberException(ErrorCode.MEMBER_ACCESS_DENIED);
    }

    public static MemberException memberAlreadyExistsException() {
        return new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }

    public static MemberException orgNotFoundException() {
        return new MemberException(ErrorCode.ORG_NOT_FOUND);
    }

    public static MemberException orgAccessDeniedException() {
        return new MemberException(ErrorCode.ORG_ACCESS_DENIED);
    }

    public static MemberException orgAlreadyExistsException() {
        return new MemberException(ErrorCode.ORG_ALREADY_EXISTS);
    }
}

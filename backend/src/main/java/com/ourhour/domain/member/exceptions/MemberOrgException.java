package com.ourhour.domain.member.exceptions;

import com.ourhour.global.exception.BusinessException;

public class MemberOrgException extends BusinessException {

    public MemberOrgException(int status, String message) {
        super(status, message);
    }

    public static MemberOrgException orgNotFoundException() {
        return new MemberOrgException(404, "해당 회사 정보를 찾을 수 없습니다.");
    }
}

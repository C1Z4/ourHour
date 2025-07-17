package com.ourhour.domain.org.exceptions;

import com.ourhour.global.exception.BusinessException;

public class OrgException extends BusinessException {

    public OrgException(int status, String message) {
        super(status, message);
    }

    public static OrgException notMuchRootAdminException() {
        return new OrgException(400, "루트 관리자는 최소 한명 이상이어야 합니다.");
    }

    public static OrgException tooMuchRootAdminException() {
        return new OrgException(400, "루트 관리자는 최대 2명이어야 합니다.");
    }
}

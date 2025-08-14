package com.ourhour.domain.org.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class OrgException extends BusinessException {

    public OrgException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static OrgException notMuchRootAdminException() {
        return new OrgException(ErrorCode.ROOT_ADMIN_MINIMUM_REQUIRED);
    }

    public static OrgException tooMuchRootAdminException() {
        return new OrgException(ErrorCode.ROOT_ADMIN_MAXIMUM_EXCEEDED);
    }

    public static OrgException lastRootAdminRemovalNotAllowed() {
        return new OrgException(ErrorCode.LAST_ROOT_ADMIN_CANNOT_LEAVE);
    }

    public static OrgException cannotSelfDeleteRootAdmin() {
        return new OrgException(ErrorCode.CANNOT_DELETE_SELF);
    }

    public static OrgException orgNotFoundException() {
        return new OrgException(ErrorCode.ORG_NOT_FOUND);
    }

    public static OrgException orgAccessDeniedException() {
        return new OrgException(ErrorCode.ORG_ACCESS_DENIED);
    }

    public static OrgException orgMemberNotFoundException() {
        return new OrgException(ErrorCode.ORG_MEMBER_NOT_FOUND);
    }

    public static OrgException orgIdNotMatchException() {
        return new OrgException(ErrorCode.ORG_ID_NOT_MATCH);
    }

}

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

    // ========== 부서 관련 예외 ==========
    public static OrgException departmentNotFoundException() {
        return new OrgException(ErrorCode.DEPARTMENT_NOT_FOUND);
    }

    public static OrgException departmentNameDuplicateException() {
        return new OrgException(ErrorCode.DEPARTMENT_NAME_DUPLICATE);
    }

    public static OrgException departmentHasMembersException() {
        return new OrgException(ErrorCode.DEPARTMENT_HAS_MEMBERS);
    }

    // ========== 직책 관련 예외 ==========
    public static OrgException positionNotFoundException() {
        return new OrgException(ErrorCode.POSITION_NOT_FOUND);
    }

    public static OrgException positionNameDuplicateException() {
        return new OrgException(ErrorCode.POSITION_NAME_DUPLICATE);
    }

    public static OrgException positionHasMembersException() {
        return new OrgException(ErrorCode.POSITION_HAS_MEMBERS);
    }

}

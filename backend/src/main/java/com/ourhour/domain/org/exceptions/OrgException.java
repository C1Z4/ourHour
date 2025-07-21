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

    public static OrgException deleteUserException(String orgName) {
        return new OrgException(403, "다음 조직에서 마지막 루트 관리자입니다. 위임 후 계정 탈퇴 가능합니다: " + orgName);
    }

    public static OrgException lastRootAdminRemovalNotAllowed() {
        return new OrgException(403, "다음 조직에서 마지막 루트 관리자입니다. 위임 후 계정 탈퇴 가능합니다");
    }

    public static OrgException cannotSelfDeleteRootAdmin() {
        return new OrgException(403, "자기 자신은 삭제할 수 없습니다. 다른 루트 관리자에게 삭제를 요청하세요.");
    }

    public static OrgException invitationException(String message) {
        return new OrgException(400, message);
    }

}

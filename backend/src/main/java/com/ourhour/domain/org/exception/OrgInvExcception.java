package com.ourhour.domain.org.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class OrgInvExcception extends BusinessException {

    public OrgInvExcception(ErrorCode errorCode) {
        super(errorCode);
    }

    public static OrgInvExcception selfInvitationNotAllowedException() {
        return new OrgInvExcception(ErrorCode.CANNOT_INVITE_SELF);
    }

}

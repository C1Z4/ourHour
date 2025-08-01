package com.ourhour.domain.project.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class MilestoneException extends BusinessException {

    public MilestoneException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MilestoneException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static MilestoneException milestoneNotFoundException() {
        return new MilestoneException(ErrorCode.MILESTONE_NOT_FOUND);
    }

    public static MilestoneException milestoneNameDuplicateException() {
        return new MilestoneException(ErrorCode.MILESTONE_NAME_DUPLICATE);
    }
}

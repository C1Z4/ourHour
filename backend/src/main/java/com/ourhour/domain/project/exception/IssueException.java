package com.ourhour.domain.project.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class IssueException extends BusinessException {

    public IssueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static IssueException issueNotFoundException() {
        return new IssueException(ErrorCode.ISSUE_NOT_FOUND);
    }

    public static IssueException issueTagNotFoundException() {
        return new IssueException(ErrorCode.ISSUE_TAG_NOT_FOUND);
    }
}

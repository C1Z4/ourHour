package com.ourhour.domain.project.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class ProjectException extends BusinessException {

    public ProjectException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProjectException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static ProjectException projectNotFoundException() {
        return new ProjectException(ErrorCode.PROJECT_NOT_FOUND);
    }

    public static ProjectException projectAccessDeniedException() {
        return new ProjectException(ErrorCode.PROJECT_ACCESS_DENIED);
    }

    public static ProjectException projectParticipantRequiredException() {
        return new ProjectException(ErrorCode.PROJECT_PARTICIPANT_REQUIRED);
    }

    public static ProjectException projectParticipantLimitInvalidException() {
        return new ProjectException(ErrorCode.PROJECT_PARTICIPANT_LIMIT_INVALID);
    }

    public static ProjectException projectParticipantOrAdminOrRootAdminException() {
        return new ProjectException(ErrorCode.PROJECT_PARTICIPANT_OR_ADMIN_OR_ROOT_ADMIN);
    }

    public static ProjectException projectIdRequiredException() {
        return new ProjectException(ErrorCode.PROJECT_ID_REQUIRED);
    }
}

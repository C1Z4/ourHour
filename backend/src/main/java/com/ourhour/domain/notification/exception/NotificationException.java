package com.ourhour.domain.notification.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class NotificationException extends BusinessException {
    
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static NotificationException notificationNotFound() {
        return new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    public static NotificationException notificationAccessDenied() {
        return new NotificationException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
    }

    public static NotificationException sseConnectionError() {
        return new NotificationException(ErrorCode.SSE_CONNECTION_ERROR);
    }
}
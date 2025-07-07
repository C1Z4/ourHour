package com.backend.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final int status;
    private final String message;
    
    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
    
    public BusinessException(String message) {
        super(message);
        this.status = 400;
        this.message = message;
    }
    
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }
    
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }
    
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }
    
    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message);
    }
} 
package com.backend.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private int status;
    private String message;
    private T data;
    
    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }
    
    // 성공 응답 생성 메서드 (커스텀 메시지)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }
    
    // 실패 응답 생성 메서드
    public static <T> ApiResponse<T> fail(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
    
    // 실패 응답 생성 메서드 (기본 400 상태코드)
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .status(400)
                .message(message)
                .data(null)
                .build();
    }
} 
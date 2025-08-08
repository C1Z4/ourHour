package com.ourhour.global.common.dto;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private HttpStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL) 
    private Integer errorCode; 

    private String message;
    private T data;

    // 성공 응답 생성 메서드 (errorCode null로 설정)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK) 
                .errorCode(null) 
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    // 성공 응답 생성 메서드 (커스텀 메시지)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .errorCode(null) 
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답 생성 메서드
    public static <T> ApiResponse<T> fail(HttpStatus httpStatus, int errorCode, String message) {
        return ApiResponse.<T>builder()
                .status(httpStatus)
                .errorCode(errorCode) 
                .message(message)
                .data(null)
                .build();
    }

    // 실패 응답 생성 메서드 (기본 400 상태코드)
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorCode(400) 
                .message(message)
                .data(null)
                .build();
    }
}

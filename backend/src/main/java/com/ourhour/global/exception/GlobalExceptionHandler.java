package com.ourhour.global.exception;

import com.ourhour.global.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // 공통 에러 응답 생성 메서드
        private ResponseEntity<ApiResponse<Object>> buildErrorResponse(
                        HttpStatus status, int code, String message) {
                log.warn("{} Exception: {}", status, message);
                return ResponseEntity.status(status)
                                .body(ApiResponse.fail(status, code, message));
        }

        // 커스텀 비즈니스 예외 처리
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
                ErrorCode errorCode = e.getErrorCode();
                return buildErrorResponse(errorCode.getStatus(), errorCode.getStatusCode(), errorCode.getMessage());
        }

        // 데이터 없음 예외 처리
        @ExceptionHandler(NoSuchElementException.class)
        public ResponseEntity<ApiResponse<Object>> handleNoSuchElementException(NoSuchElementException e) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, 404, "요청한 데이터를 찾을 수 없습니다.");
        }

        // 유효성 검증 실패 예외 처리 (@Valid)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
                String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
                return buildErrorResponse(HttpStatus.BAD_REQUEST, 400, errorMessage);
        }

        // 파라미터 타입 불일치 예외 처리
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, 400, "잘못된 파라미터 형식입니다.");
        }

        // 제약 조건 위반 예외 처리
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
                // 실제 validation 메시지 추출
                String errorMessage = e.getConstraintViolations()
                                .stream()
                                .findFirst()
                                .map(violation -> violation.getMessage())
                                .orElse("입력값이 유효하지 않습니다.");

                return buildErrorResponse(HttpStatus.BAD_REQUEST, 400, errorMessage);
        }

        // 일반적인 예외 처리
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
                log.error("Unexpected Exception: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 내부 오류가 발생했습니다."));
        }
}
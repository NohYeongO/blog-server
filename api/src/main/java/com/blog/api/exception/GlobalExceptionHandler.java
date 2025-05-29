package com.blog.api.exception;

import com.blog.board.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Post 관련 익셉션들
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e) {
        log.warn("Post not found: {}", e.getMessage());
        return createErrorResponse(e.getErrorCode().getStatus(), "P001", e.getErrorCode().getMessage());
    }

    // Category 관련 익셉션들
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException e) {
        log.warn("Category not found: {}", e.getMessage());
        return createErrorResponse(e.getErrorCode().getStatus(), "C001", e.getErrorCode().getMessage());
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException e) {
        log.warn("Category already exists: {}", e.getMessage());
        return createErrorResponse(e.getErrorCode().getStatus(), "C002", e.getErrorCode().getMessage());
    }

    // 접근 권한 관련 익셉션
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return createErrorResponse(HttpStatus.FORBIDDEN, "A001", e.getMessage());
    }

    // 일반적인 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected runtime exception: ", e);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "서버 내부 오류가 발생했습니다.");
    }

    // 모든 예외의 최종 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception: ", e);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "예상치 못한 오류가 발생했습니다.");
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String code, String message) {
        return createErrorResponse(status, code, message, null);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String code, String message, Map<String, String> errors) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .errors(errors)
                .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}

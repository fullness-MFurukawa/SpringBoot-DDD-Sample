package com.example.ddd_demo.presentation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.infrastructure.exception.InternalException;

import jakarta.validation.ConstraintViolationException;

/**
 * プレゼンテーション層の共通例外ハンドラ
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    /** 入力値バリデーション不正（@NotBlank など）→ 400 */
    @ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class })
    public ResponseEntity<String> handleValidation(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // @RequestParam, @PathVariable, @RequestHeader等のバリデーション用
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        // メッセージ整形はお好みで
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(msg);
    }


    /** DTO→Domain 変換時のアプリ層の入力不正 → 400 */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInput(InvalidInputException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /** ドメインルール違反（VOの自己検証など） → 400 */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomain(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /** 見つからない系 → 404 */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /** リソース重複（登録済み）→ 409 Conflict */
    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<String> handleExists(ExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /** 内部例外（DBアクセスエラーなど）→ 500 Internal Server Error */
    @ExceptionHandler(InternalException.class)
    public ResponseEntity<String> handleInternal(InternalException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Internal Error: " + ex.getMessage());
    }

    /** 想定外 → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
}

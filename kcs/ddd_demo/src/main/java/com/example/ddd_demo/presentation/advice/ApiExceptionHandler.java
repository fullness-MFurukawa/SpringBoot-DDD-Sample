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
 * 🎯 {@code @RestControllerAdvice} による全体例外ハンドラ
 * <p>
 * プレゼンテーション層の最上位で例外を一括的に捕捉し、
 * 適切なHTTPステータスコードとレスポンスメッセージを返却する。
 * </p>
 *
 * <h3>🧩 主な役割</h3>
 * <ul>
 *   <li>Controller層でスローされた例外を一元的に処理する</li>
 *   <li>アプリケーション／ドメイン層で発生した例外を
 *       適切なHTTPレスポンスへ変換する</li>
 *   <li>AOPの <b>AfterThrowing Advice</b> として動作し、
 *       SpringMVC(DispatcherServlet)経由で呼び出される</li>
 * </ul>
 *
 * <h3>💡 設計意図</h3>
 * 各層の責務を明確化し、「例外は投げる」「ハンドリングはAdviceで行う」
 * という責務分離を実現する。
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    /**
     * 💬 BeanValidation (@NotBlank, @Min 等) の検証エラー。
     * <p>DTO／RequestBodyのバリデーション不正時にスローされる。</p>
     *
     * @param ex MethodArgumentNotValidException または IllegalArgumentException
     * @return HTTP 400 (Bad Request)
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class })
    public ResponseEntity<String> handleValidation(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * 💬 @RequestParam, @PathVariable などのメソッドパラメータバリデーションエラー。
     * <p>{@link ConstraintViolationException} のメッセージを整形して返却する。</p>
     *
     * @param ex ConstraintViolationException
     * @return HTTP 400 (Bad Request)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        // メッセージ整形はお好みで
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(msg);
    }


    /**
     * 💬 DTO→ドメイン変換中に発生した入力不正。
     * <p>アプリケーション層で {@link InvalidInputException} がスローされた場合に対応。</p>
     *
     * @param ex InvalidInputException
     * @return HTTP 400 (Bad Request)
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInput(InvalidInputException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * 💬 ドメインルール違反。
     * <p>ドメイン層で不変条件違反などにより {@link DomainException} が発生した場合に対応。</p>
     *
     * @param ex DomainException
     * @return HTTP 400 (Bad Request)
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomain(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * 💬 指定されたリソースが存在しない場合。
     * <p>検索・参照対象が見つからなかった場合に {@link NotFoundException} がスローされる。</p>
     *
     * @param ex NotFoundException
     * @return HTTP 404 (Not Found)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * 💬 リソース重複（登録済み）。
     * <p>既存データと重複する登録要求時に {@link ExistsException} がスローされる。</p>
     *
     * @param ex ExistsException
     * @return HTTP 409 (Conflict)
     */
    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<String> handleExists(ExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * 💬 内部的な障害（DBアクセス、通信エラーなど）。
     * <p>インフラ層で発生する {@link InternalException} を処理する。</p>
     *
     * @param ex InternalException
     * @return HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(InternalException.class)
    public ResponseEntity<String> handleInternal(InternalException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Internal Error: " + ex.getMessage());
    }

    /**
     * 💬 想定外の例外（上記以外のすべて）。
     * <p>システム例外など、ハンドリングされなかった例外の最終的なフォールバック。</p>
     *
     * @param ex Exception
     * @return HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
}

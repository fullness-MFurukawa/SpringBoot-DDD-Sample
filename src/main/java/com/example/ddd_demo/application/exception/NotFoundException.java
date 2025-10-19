package com.example.ddd_demo.application.exception;

/**
 * データが存在しないことを表す例外クラス
 */
public class NotFoundException extends RuntimeException{
    /**
     * コンストラクタ
     * @param message エラーメッセージ
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

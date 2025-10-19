package com.example.ddd_demo.application.exception;
/**
 * データが既に存在することを表す例外クラス
 */
public class ExistsException extends RuntimeException{
    /**
     * コンストラクタ
     * @param message エラーメッセージ
     */
    public ExistsException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public ExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

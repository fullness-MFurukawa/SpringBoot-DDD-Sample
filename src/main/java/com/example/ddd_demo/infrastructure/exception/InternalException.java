package com.example.ddd_demo.infrastructure.exception;

/**
 * データベース停止などの内部エラーを表す例外クラス
 */
public class InternalException extends RuntimeException{
    /**
     * コンストラクタ
     * @param message エラーメッセージ
     */
    public InternalException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}

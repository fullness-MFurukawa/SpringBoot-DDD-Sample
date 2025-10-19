package com.example.ddd_demo.application.exception;

/**
 * アプリケーション層での入力データ不正例外
 * - DTOの値が不正または変換できない場合にスローされる
 */
public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}

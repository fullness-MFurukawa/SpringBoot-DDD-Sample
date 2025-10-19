package com.example.ddd_demo.domain.exception;

/**
 * 値オブジェクトやエンティティが持つ不変条件・ビジネスルールの
 * 違反を表現するために使用する例外クラス
 */
public class DomainException extends RuntimeException{
    /**
     * ドメイン例外をメッセージ付きで生成する
     * 
     * @param message ドメインルール違反メッセージ
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * ドメイン例外をメッセージと原因例外付きで生成する。
     * 
     * @param message ドメインルール違反の説明メッセージ
     * @param cause 原因となった例外（任意）
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

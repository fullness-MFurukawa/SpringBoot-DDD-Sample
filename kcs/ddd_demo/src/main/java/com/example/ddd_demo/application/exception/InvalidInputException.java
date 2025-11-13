package com.example.ddd_demo.application.exception;

 /**
 * {@code InvalidInputException} は、アプリケーション層で受け取った
 * 入力データが不正または変換不可能であることを表す実行時例外です。
 *
 * <p>DTOの変換、入力値の検証、MapperやAssemblerによる整合性チェック中に発生します。
 * 例えば、文字列がUUID形式でない、価格が負数である、関連IDがnullなどのケースです。
 *
 * <p>この例外は外部から渡された入力データがビジネスルールに
 * 到達する前に弾かれたことを意味します。
 *
 * <p>例：
 * <pre>{@code
 * if (dto.getPrice() < 0) {
 *     throw new InvalidInputException("価格は0以上でなければなりません。");
 * }
 * }</pre>
 *
 * <p>層の責務：
 * <ul>
 *   <li>発生層：アプリケーション層（Assembler、Mapper）</li>
 *   <li>捕捉層：ControllerまたはExceptionHandler（HTTP 400 Bad Requestに変換）</li>
 * </ul>
 */
public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}

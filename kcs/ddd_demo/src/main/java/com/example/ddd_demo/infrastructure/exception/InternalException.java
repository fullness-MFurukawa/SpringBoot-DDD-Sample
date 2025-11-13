package com.example.ddd_demo.infrastructure.exception;

/**
 * システム内部で発生する「技術的な異常状態」を表す例外クラス。
 * <p>
 * 主に以下のような、アプリケーション外部からの操作では回避できない
 * 技術的エラーを通知するために使用します。
 * </p>
 *
 * <ul>
 *   <li>データベースサーバの停止・接続障害</li>
 *   <li>外部API通信の失敗やタイムアウト</li>
 *   <li>ファイル入出力エラー、設定ファイルの不整合</li>
 * </ul>
 *
 * <p>
 * これらはドメインルールの違反ではなく、インフラストラクチャ層の
 * 「技術的な問題」に分類されます。<br>
 * ドメイン層では {@code DomainException}、インフラ層では本クラスを使い分けることで、
 * 業務上のエラーと技術的エラーを明確に区別できます。
 * </p>
 *
 * <p>
 * 【使用例】
 * <pre>{@code
 * try {
 *     jdbcTemplate.queryForObject(sql, params, mapper);
 * } catch (SQLException ex) {
 *     throw new InternalException("DB接続中にエラーが発生しました。", ex);
 * }
 * }</pre>
 * </p>
 */
public class InternalException extends RuntimeException{
    /**
     * 指定したメッセージで技術的例外を生成します。
     *
     * @param message エラーの内容を示すメッセージ
     */
    public InternalException(String message) {
        super(message);
    }

    /** 
     * 指定したメッセージと原因例外で技術的例外を生成します。
     *
     * @param message エラーの内容を示すメッセージ
     * @param cause 原因となった例外（例: DataAccessException, IOExceptionなど）
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}

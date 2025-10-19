package com.example.ddd_demo.domain.models.product;

import java.util.Objects;
import java.util.UUID;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * 商品を一意に識別する値オブジェクト
 * - 不変／自己検証／値で等価
 * - 内部表現はUUIDのcanonical文字列（小文字・ハイフン付き、36文字）
 */
public final class ProductId {
    /** 
     * canonicalなUUID文字列(不変) 
     */
    private final String value;

    /** 
     * コンストラクタ
     * 外部から直接は生成させない(不変保証のため)
     */
    private ProductId(String value) {
        this.value = value;
    }

    /**
     * 新しいUUIDを発行してProductIdを生成する。
     * UUID#toString は canonical(小文字・ハイフン付き)で返る。
     * @return 新規生成された ProductId
     */
    public static ProductId createNew() {
        String canonical = UUID.randomUUID().toString();
        return new ProductId(canonical);
    }

    /**
     * 既存のUUID文字列からProductIdを復元する。
     * 入力は大文字/小文字を問わず受け付けるが、内部ではcanonical(小文字)に正規化する
     * @param raw UUID文字列(ハイフン付き36文字)
     * @return 検証済みのProductId
     * @throws DomainException UUID形式でない場合
     */
    public static ProductId fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("ProductIdは必須です。");
        }
        String s = raw.trim();
        // UUIDの厳密検証（8-4-4-4-12形式）
        if (!s.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new DomainException("ProductIdはUUID形式で指定してください。: " + raw);
        }
        // UUIDとして正規化（常に小文字・ハイフン付き）
        String canonical = UUID.fromString(s).toString();
        return new ProductId(canonical);
    }

    /**
     * 正規化済みのUUID文字列を返す
     * 常に小文字・ハイフン付き・36文字
     */
    public String value() {
        return value;
    }

    /** 
     * 現在保持している値を文字列として返す 
     */
    @Override
    public String toString() {
        return value;
    }

    /** 
     * 値で等価判定(同じUUID文字列なら等価） 
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductId)) return false;
        ProductId that = (ProductId) o;
        return value.equals(that.value);
    }
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

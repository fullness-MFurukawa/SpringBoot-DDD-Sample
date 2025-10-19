package com.example.ddd_demo.domain.models.stock;

import java.util.Objects;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * DDDエンティティ: Stock（商品在庫）
 * - 同一性: StockId（値オブジェクト）
 * - 属性: StockQuantity（0〜100、自己検証VO）
 */
public final class Stock {
    /** 在庫の同一性（不変） */
    private final StockId stockId;
    /** 在庫数（VO） */
    private StockQuantity quantity;

    /**
     * 新規作成
     * @param initialQuantity 初期在庫数
     * @return Stockエンティティ
     */
    public static Stock createNew(StockQuantity initialQuantity) {
        if (initialQuantity == null) {
            throw new DomainException("在庫数は必須です。");
        }
        return new Stock(StockId.createNew(), initialQuantity);
    }

    /**
     * 生成: 識別子を指定して再構築(リストア)
     * - 既存データの復元やテストの明示的なID指定に利用する
     */
    public static Stock rehydrate(StockId id, StockQuantity quantity) {
        return new Stock(id, quantity);
    }

    /**
     * コンストラクタ（不変条件の検証を集約）
     */
    private Stock(StockId id, StockQuantity quantity) {
        if (id == null)       throw new DomainException("在庫IDは必須です。");
        if (quantity == null) throw new DomainException("在庫数は必須です。");
        this.stockId = id;
        this.quantity = quantity;
    }

    /**
     * 在庫数を加算する(0以上の整数)
     * 加算後の値が0〜100の範囲外になる場合、StockQuantity.of(...)がDomainExceptionをスローする
     */
    public void increase(int amount) {
        if (amount < 0) throw new DomainException("在庫の増分は0以上で指定してください。: " + amount);
        int newValue = this.quantity.value() + amount;
        this.quantity = StockQuantity.of(newValue);
    }

    
    /**
     * 在庫数を減算する(0以上の整数)
     * 減算後の値が0〜100の範囲外になる場合、StockQuantity.of(...)がDomainExceptionをスローする
     */
    public void decrease(int amount) {
        if (amount < 0) throw new DomainException("在庫の減分は0以上で指定してください。: " + amount);
        int newValue = this.quantity.value() - amount;
        this.quantity = StockQuantity.of(newValue);
    }

    /**
     * 在庫数を変更する
     */
    public void changeQuantity(StockQuantity newQuantity) {
        if (newQuantity == null) throw new DomainException("在庫数は必須です。");
        this.quantity = newQuantity;
    }

    /**
     * ゲッター
     */
    public StockId getStockId()            { return stockId; }
    public StockQuantity getQuantity()     { return quantity; }
    /**
     * 在庫なし
     */
    public boolean isOutOfStock()       { return quantity.value() == StockQuantity.MIN; }
    /**
     * 在庫いっぱい
     */
    public boolean isFullCapacity()     { return quantity.value() == StockQuantity.MAX; }

    /** 
     * 同一性による等価
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock other = (Stock) o;
        return Objects.equals(stockId, other.stockId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(stockId);
    }

    /**
     * インスタンスの内容
     */
    @Override
    public String toString() {
        return "Stock{id=" + stockId + ", quantity=" + quantity + "}";
    }
}

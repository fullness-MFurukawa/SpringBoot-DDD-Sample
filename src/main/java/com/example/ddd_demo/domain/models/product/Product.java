package com.example.ddd_demo.domain.models.product;

import java.util.Objects;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * 商品を表すエンティティ
 * - 同一性: ProductId（値オブジェクト）
 * - 属性: ProductName / ProductPrice（いずれも不変・自己検証VO）
 */
public class Product {
    /** 
     * 商品の同一性（不変） 
     */
    private final ProductId productId;
    /** 
     * 商品名（VO） 
     */
    private ProductName name;
    /** 
     * 商品単価（VO） 
     */
    private ProductPrice price;
    /** 
     * 商品カテゴリId（VO） 
     */
    private final CategoryId categoryId;
    /**
     * 商品在庫(Entity)
     */
    private Stock stock;

    /**
     * 新規作成
     * @param name 商品名
     * @param price 商品単価
     * @return 商品エンティティ
     */
    public static Product createNew(
        ProductName name, ProductPrice price, 
         CategoryId categoryId, StockQuantity quantity) {
        if (name == null) throw new DomainException("商品名は必須です。");
        if (price == null) throw new DomainException("商品単価は必須です。");
        if (categoryId == null) throw new DomainException("商品カテゴリIdは必須です。");
        if (quantity == null) throw new DomainException("初期在庫は必須です。");
        return new Product(ProductId.createNew(), name, price, categoryId, Stock.createNew(quantity));
    }

    /**
     * 生成: 識別子を指定して再構築(リストア)
     * - 既存データの復元やテストの明示的なID指定に利用する
     */
    public static Product rehydrate(
        ProductId id, ProductName name, 
        ProductPrice price, CategoryId categoryId, Stock stock) {
        return new Product(id, name, price , categoryId, stock);
    }

    /**
     * コンストラクタ（不変条件の検証を集約）
     */
    private Product(
        ProductId id, ProductName name, 
        ProductPrice price, CategoryId categoryId, Stock stock) {
        if (id == null)    throw new DomainException("商品IDは必須です。");
        if (name == null)  throw new DomainException("商品名は必須です。");
        if (price == null) throw new DomainException("商品単価は必須です。");
        if (categoryId == null) throw new DomainException("商品カテゴリIdは必須です。");
        if (stock == null) throw new DomainException("在庫は必須です。");
        this.productId = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId; 
        this.stock = stock;
    }

    /**
     * 商品名を変更する
     */
    public void rename(ProductName newName) {
        if (newName == null) throw new DomainException("商品名は必須です。");
        this.name = newName;
    }

    /**
     * 単価を変更する
     */
    public void reprice(ProductPrice newPrice) {
        if (newPrice == null) throw new DomainException("商品単価は必須です。");
        this.price = newPrice;
    }

    /**
     * 在庫を変更する
     * @param newQty 新しい在庫
     */
    public void changeStock(StockQuantity newQty) {
        ensureStockAttached();
        this.stock.changeQuantity(newQty);
    }

    private void ensureStockAttached() {
        if (this.stock == null) {
            throw new DomainException("在庫が未設定です。先に attachStock(...) を呼び出してください。");
        }
    }

    /** 
     * ゲッター
     */
    public ProductId getProductId() { return productId; }
    public ProductName getName()    { return name; }
    public ProductPrice getPrice()  { return price; }
    public CategoryId getCategoryId() { return categoryId; }
    public StockQuantity currentStock() { return stock.getQuantity(); }
    public Stock getStock() {return stock; }

    /**
     * 同一性による等価性 
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return Objects.equals(productId, other.productId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    /**
     * インスタンスの内容
     */
    @Override
    public String toString() {
        return 
        "Product{id=" + productId + ", name=" + name + ", price=" + price + "cetgroyId=" + categoryId + "}";
    }
}

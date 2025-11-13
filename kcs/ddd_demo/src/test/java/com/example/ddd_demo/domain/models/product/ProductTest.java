package com.example.ddd_demo.domain.models.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * 商品エンティティ: Product の単体テストドライバ
 * - 生成/再構築、リネーム、例外、不変条件、等価性(hashCode含む)を検証
 * - Category をエンティティとして内包する現在の仕様に対応
 */
@DisplayName("Product エンティティの単体テスト")
public class ProductTest {
   
    // ===== ヘルパー: 値オブジェクト / エンティティ組み立て
    private static ProductName n(String s)       { return ProductName.of(s); }
    private static ProductPrice p(int v)         { return ProductPrice.of(v); }
    private static ProductId id(String s)        { return ProductId.fromString(s); }
    private static CategoryId cid(String s)      { return CategoryId.fromString(s); }
    private static CategoryName cname(String s)  { return CategoryName.of(s); }
    /** CategoryエンティティをUUIDと名前から再構築 */
    private static Category cat(String uuid, String name) {
        return Category.restore(cid(uuid), cname(name));
    }


    @Test
    @DisplayName("createNew(): 新規作成で商品Idが自動発番され、商品名と単価が設定される")
    void createNew_ShouldGenerateId_AndKeepNamePrice() {
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product product = Product.createNew(
            n("ワイヤレスマウス"), p(900),
            category, StockQuantity.of(20)
        );

        // 商品Idは自動発番
        assertNotNull(product.getProductId());
        // 商品名を保持している           
        assertEquals("ワイヤレスマウス", product.getName().value());
        // 単価を保持している
        assertEquals(900, product.getPrice().value());
        // カテゴリを保持している
        assertEquals(category, product.getCategory());
        // 商品在庫を保持している
        assertEquals(20, product.currentStock().value());
    }

    @Test
    @DisplayName("createNew(): nameがnullならDomainExceptionをスローする")
    void createNew_NullName_ShouldThrow() {

        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");
        // DomainExceptionがスローされたことを検証する
        DomainException ex = assertThrows(DomainException.class,
            () -> Product.createNew(null, p(900), category, StockQuantity.of(20)));
        // エラーメッセージを検証する
        assertEquals("商品名は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("createNew(): priceがnullならDomainExceptionをスローする")
    void createNew_NullPrice_ShouldThrow() {
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");
        // DomainExceptionがスローされたことを検証する
        DomainException ex = assertThrows(DomainException.class,
            () -> Product.createNew(n("ワイヤレスマウス"), null, category, StockQuantity.of(20)));
        // エラーメッセージを検証する
        assertEquals("商品単価は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("rehydrate(): 指定したID/名前/単価で再構築できる")
    void rehydrate_ShouldBuildWithGivenIdNamePrice() {

        var existing = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        var stock = Stock.createNew(StockQuantity.of(20));
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");
         // 商品エンティティを生成する
        Product product = Product.restore(existing, n("無線式キーボード"), p(1900), category, stock);
        // 保持している値を検証する
        assertEquals(existing, product.getProductId());
        assertEquals("無線式キーボード", product.getName().value());
        assertEquals(1900, product.getPrice().value());
        assertEquals(category, product.getCategory());
        assertEquals(20, product.currentStock().value());
    }

    @Test
    @DisplayName("rehydrate(): 商品IdがnullならDomainExceptionをスローする")
    void rehydrate_NullId_ShouldThrow() {

        var stock = Stock.createNew(StockQuantity.of(20));
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        DomainException ex = assertThrows(DomainException.class,
            () -> Product.restore(null, n("無線式キーボード"), p(1900), category, stock));
        assertEquals("商品IDは必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("rehydrate(): 商品名がnullならDomainExceptionをスローする")
    void rehydrate_NullName_ShouldThrow() {
        var existing = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        var stock = Stock.createNew(StockQuantity.of(20));
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        DomainException ex = assertThrows(DomainException.class,
            () -> Product.restore(existing, null, p(1900), category, stock));
        assertEquals("商品名は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("rehydrate(): 商品単価がnullならDomainExceptionをスローする")
    void rehydrate_NullPrice_ShouldThrow() {
        var existing = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        var stock = Stock.createNew(StockQuantity.of(20));
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        DomainException ex = assertThrows(DomainException.class,
            () -> Product.restore(existing, n("無線式キーボード"), null, category, stock));
        assertEquals("商品単価は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("rename(): 有効な商名で変更できる")
    void rename_ShouldUpdateName() {
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product product = Product.createNew(n("ワイヤレスマウス"), p(900),
                category, StockQuantity.of(20));
        product.rename(n("無線式マウス"));

        assertEquals("無線式マウス", product.getName().value());
    }

    @Test
    @DisplayName("rename(): 商品名がnullならDomainExceptionがスローされる")
    void rename_Null_ShouldThrow() {
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product product = Product.createNew(n("ワイヤレスマウス"), p(900), category, StockQuantity.of(20));

        DomainException ex = assertThrows(DomainException.class, () -> product.rename(null));
        assertEquals("商品名は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("reprice(): 有効な金額で単価を変更できる")
    void reprice_ShouldUpdatePrice() {
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product product = Product.createNew(n("ワイヤレスマウス"), p(900), category, StockQuantity.of(20));
        product.reprice(p(1000));

        assertEquals(1000, product.getPrice().value());
    }

    @Test
    @DisplayName("reprice(): 金額がnullならDomainExceptionがスローされる")
    void reprice_Null_ShouldThrow() {
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product product = Product.createNew(n("ワイヤレスマウス"), p(900), category, StockQuantity.of(20));

        DomainException ex = assertThrows(DomainException.class, () -> product.reprice(null));
        assertEquals("商品単価は必須です。", ex.getMessage());
    }
    
    @Test
    @DisplayName("equals/hashCode: 同一商品Idなら等価、異なれば非等価")
    void equalsHashCode_ShouldUseIdentity() {
        var sameId = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        var stock = Stock.createNew(StockQuantity.of(20));
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product a = Product.restore(sameId, n("A"), p(100), category, stock);
        Product b = Product.restore(sameId, n("B"), p(200), category, stock);
        Product c = Product.restore(
            id("8f81a72a-58ef-422b-b472-d982e8665292"), n("C"), p(300), category, stock
        );

        // 商品Idが同じなら等価
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // 商品Idが異なれば非等価
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("toString(): スモーク（例外なく意味のある文字列を返す）")
    void toString_Smoke() {
         var stock = Stock.createNew(StockQuantity.of(20));
        var category = cat("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");

        Product product = Product.restore(
            id("ac413f22-0cf1-490a-9635-7e9ca810e544"),
            n("ワイヤレスマウス"),
            p(900),
            category,
            stock
        );

        String s = product.toString();

        // 例外が出ず、代表的な情報が含まれることを確認
        assertTrue(s.contains("Product"));
        assertTrue(s.contains("ワイヤレスマウス"));
        assertTrue(s.contains("900"));
        assertTrue(s.contains("ac413f22-0cf1-490a-9635-7e9ca810e544"));

        // Category.toString()
        assertTrue(s.contains(category.getCategoryId().value()));
        assertTrue(s.contains(category.getName().value()));
    }
}

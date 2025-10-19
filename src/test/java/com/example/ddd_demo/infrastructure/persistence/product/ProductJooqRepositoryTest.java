package com.example.ddd_demo.infrastructure.persistence.product;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * ProductRepositoryインターフェイス実装のテストドライバ
 */
@SpringBootTest
@Transactional 
public class ProductJooqRepositoryTest {

    /**
     * テストターゲット
     */
    @Autowired
    private ProductJooqRepository repository;
   
    @Test
    @DisplayName("findById(): 存在する商品Idの場合は該当商品を取得できる")
    void findById_found() {
        ProductId id = ProductId.fromString("9959e553-c9da-4646-bd85-8663a3541583");
        // 商品Idで検索する
        Optional<Product> found = repository.findById(id);
        // 検索結果が存在することを検証する
        assertThat(found).isPresent();
        // 検索結果を取り出す
        Product p = found.get();
        // 商品Idを検証する
        assertThat(p.getProductId().value()).isEqualTo("9959e553-c9da-4646-bd85-8663a3541583");
        // 商品名を検証する
        assertThat(p.getName().value()).isEqualTo("油性ボールペン(黒)");
        // 単価を検証する
        assertThat(p.getPrice().value()).isEqualTo(100);
        // 在庫数を検証する
        assertThat(p.currentStock().value()).isEqualTo(100);
    }

    @Test
    @DisplayName("findById(): 存在しない商品Idの場合はOptional.empty()が返される")
    void findById_notFound() {
        ProductId id = ProductId.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        // 商品Idで検索する
        Optional<Product> found = repository.findById(id);
        // emptyであることを検証する
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findById(): nullを渡すとDomainExceptionをスローする")
    void findById_null_throws() {
        assertThatThrownBy(() -> repository.findById(null))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("商品Idは必須です。");
    }

    @Test
    @DisplayName("existsByName(): 既存の商品名ならtrueを返す")
    void existsByName_found() {
        var result = repository.existsByName(ProductName.of("蛍光ペン(黄)"));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByName(): 存在しない商品名ならfalseを返す")
    void existsByName_notFound() {
        var result = repository.existsByName(ProductName.of("消しゴム"));
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("existsByName(): nullを渡すとDomainException")
    void existsByName_null_throws() {
        assertThatThrownBy(() -> repository.existsByName(null))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("商品名は必須です。");
    }

    @Test
    @DisplayName("create(): 商品と在庫が登録され、findById()で確認できる")
    void create_and_readback() {
        // 登録データを用意する
        ProductName name = ProductName.of("シャープペンシル");
        ProductPrice price = ProductPrice.of(150);
        CategoryId categoryId = CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
        StockQuantity qty = StockQuantity.of(30);
        // 商品エンティティを生成する
        Product product = Product.createNew(name, price, categoryId, qty);
        // 商品を永続化する
        repository.create(product);
        // 永続化した商品を取得する
        Optional<Product> found = repository.findById(product.getProductId());
        // 取得できたことを検証する
        assertThat(found).isPresent();
        // 結果を取り出す
        Product rec = found.get();
        // 商品名を検証する
        assertThat(rec.getName()).isEqualTo(name);
        // 単価を検証する
        assertThat(rec.getPrice()).isEqualTo(price);
        // 在庫数を検証する
        assertThat(rec.currentStock()).isEqualTo(qty);
        // 商品カテゴリIdを検証する
        assertThat(rec.getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("create(): 存在しないカテゴリId(UUID)ならDomainExceptionをスローする")
    void create_with_unknown_category_throws() {
        // データを用意する
        ProductName name = ProductName.of("ホッチキス");
        ProductPrice price = ProductPrice.of(220);
        CategoryId unknown = CategoryId.fromString(java.util.UUID.randomUUID().toString());
        StockQuantity qty = StockQuantity.of(5);
        // 商品を永続化する
        Product product = Product.createNew(name, price, unknown, qty);
        // DomainExceptionがスローされたことを検証する
        assertThatThrownBy(() -> repository.create(product))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("指定された商品カテゴリが存在しません。");
    }

    @Test
    @DisplayName("findByName(): 登録済み商品名で取得できる")
    void findByName_found() {
        // データを用意する
        var productId = "e4850253-f363-4e79-8110-7335e4af45be";
        var name = "鉛筆(黒)";
        var price = 100;
        var quantity = 100;
        var categoryId = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";
       
        // 鉛筆(黒)を検索する
        var found = repository.findByName(ProductName.of(name));

        // データが存在することを検証する
        assertThat(found).isPresent();
        // データを取り出す
        Product p = found.get();
        // 商品Idを検証する
        assertThat(p.getProductId().value()).isEqualTo(productId);
        // 商品名を検証する
        assertThat(p.getName().value()).isEqualTo(name);
        // 商品単価を検証する
        assertThat(p.getPrice().value()).isEqualTo(price);
        // 商品在庫数を検証する
        assertThat(p.currentStock().value()).isEqualTo(quantity);
        // 商品カテゴリIdを検証する
        assertThat(p.getCategoryId().value()).isEqualTo(categoryId);
    }

    
    @Test
    @DisplayName("findByName(): 存在しない商品名ならemptyを返す")
    void findByName_notFound() {
        var result = repository.findByName(ProductName.of("存在しない商品名"));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByName(): nullはDomainException")
    void findByName_null_throws() {
        assertThatThrownBy(() -> repository.findByName(null))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("商品名は必須です。");
    }
}

package com.example.ddd_demo.application.product.service.impl;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.product.service.ProductService;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.product.ProductRepository;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * 商品サービスインターフェイスインターフェイス実装のテストドライバ
 */
@SpringBootTest
@Transactional
public class ProductServiceImplTest {

    /**
     * テストターゲット
     */
    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;


    @Test
    @DisplayName("existsProduct(): 同名商品が存在するならExistsExceptionをスロするる")
    void existsProduct_exists_throws() {
        assertThatThrownBy(() -> service.existsProduct(ProductName.of("蛍光ペン(黄)")))
            .isInstanceOf(ExistsException.class)
            .hasMessageContaining(
                String.format("商品名:[%s]は既に登録済みです。", "蛍光ペン(黄)"));
    }

    @Test
    @DisplayName("existsProduct(): 存在しなければ何も起こらない")
    void existsProduct_notExists_ok() {
        assertThatCode(() -> service.existsProduct(ProductName.of("存在しない商品")))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addProduct(): 商品と在庫が登録される")
    void addProduct_inserts_product_and_stock() {
        // Categoryはエンティティで渡す（Repository内でUUID→PKに解決）
        var category = Category.restore(
	        CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4"),
            CategoryName.of("文房具"));
        var product = Product.createNew(
            ProductName.of("ペーパーナイフ"),
            ProductPrice.of(1200),
            category,
            StockQuantity.of(10)
        );
        // 商品を登録する
        service.addProduct(product);
        // 登録データを取得する
        var found = repository.findByName(ProductName.of("ペーパーナイフ"));
        // 存在することを評価する
        assertThat(found).isPresent();
        // データを取得する
        var p = found.get();
        // 商品名を検証する
        assertThat(p.getName()).isEqualTo(ProductName.of("ペーパーナイフ"));
        // 単価を検証する
        assertThat(p.getPrice()).isEqualTo(ProductPrice.of(1200));
        // 在庫数を検証する
        assertThat(p.currentStock()).isEqualTo(StockQuantity.of(10));
        // 商品カテゴリIdを検証する
        assertThat(p.getCategory().getCategoryId().value())
            .isEqualTo("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
    }

    @Test
    @DisplayName("getProductByName(): 登録済み商品名を指定した場合、該当Productを返す")
    void getProductByName_found() {
        var name = ProductName.of("蛍光ペン(赤)");

        var result = service.getProductByName(name);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(ProductName.of("蛍光ペン(赤)"));
        assertThat(result.getPrice()).isEqualTo(ProductPrice.of(130));
        assertThat(result.currentStock()).isEqualTo(StockQuantity.of(100));
    }

    @Test
    @DisplayName("getProductByName(): 存在しない商品名を指定した場合、NotFoundExceptionをスローする")
    void getProductByName_notFound() {
        var name = ProductName.of("ペーパーナイフ");

        assertThatThrownBy(() -> service.getProductByName(name))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("商品名:[ペーパーナイフ]の商品は存在しません。");
    }

      @Test
    @DisplayName("getProductById(): 既存のIDを指定した場合、対応する商品を取得できる")
    void getProductById_found() {
        // Arrange
        var existingId = ProductId.fromString("ac413f22-0cf1-490a-9635-7e9ca810e544"); // 例: 文房具カテゴリの商品ID

        // Act
        Product product = service.getProductById(existingId);

        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getProductId().value()).isEqualTo(existingId.value());
        assertThat(product.getName().value()).isEqualTo("水性ボールペン(黒)");
    }

    @Test
    @DisplayName("getProductById(): 存在しないIDを指定した場合、NotFoundExceptionをスロするる")
    void getProductById_notFound_throws() {
        // Arrange
        var unknownId = ProductId.createNew(); // ランダムUUID（存在しない商品）

        // Act & Assert
        assertThatThrownBy(() -> service.getProductById(unknownId))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(
                String.format("商品Id:[%s]の商品は存在しません。", unknownId.value()));
    }
}

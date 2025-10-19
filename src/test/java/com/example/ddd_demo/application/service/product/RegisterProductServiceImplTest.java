package com.example.ddd_demo.application.service.product;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.product.ProductRepository;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * 商品登録サービスインターフェイスの実装テストドライバ
 */
@SpringBootTest
@Transactional
public class RegisterProductServiceImplTest {
    /**
     * テストターゲット
     */
    @Autowired
    private RegisterProductService service;
    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("getCategories(): すべてのカテゴリが取得できる")
    void getCategories_returns_all() {
        List<Category> categories = service.getCategories();

        assertThat(categories)
            .extracting(c -> c.getName().value())
            .contains("文房具", "雑貨" ,"パソコン周辺機器");
    }

    @Test
    @DisplayName("getCategoryById(): 既存の商品カテゴリIdで商品カテゴリ取得できる")
    void getCategoryById_found() {
        
        var categoryId = CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
        var category = service.getCategoryById(categoryId);

        assertThat(category.getCategoryId().value()).isEqualTo(categoryId.value());
        assertThat(category.getName().value()).isEqualTo("文房具");
    }

    @Test
    @DisplayName("getCategoryById(): 存在しない商品カテゴリIdならNotFoundExceptionをスローする")
    void getCategoryById_notFound_throws() {
        var unknown = CategoryId.fromString(UUID.randomUUID().toString());
        assertThatThrownBy(() -> service.getCategoryById(unknown))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(
                String.format("商品カテゴリId:[%s]の商品カテゴリは存在しません。", unknown));
    }

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
        var product = Product.createNew(
            ProductName.of("ペーパーナイフ"),
            ProductPrice.of(1200),
            CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4"),
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
        assertThat(p.getCategoryId().value()).isEqualTo("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
    }

    @Test
    @DisplayName("getProducyByName(): 登録済み商品名を指定した場合、該当Productを返す")
    void getProductByName_found() {
        var name = ProductName.of("蛍光ペン(赤)");

        var result = service.getProducyByName(name);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(ProductName.of("蛍光ペン(赤)"));
        assertThat(result.getPrice()).isEqualTo(ProductPrice.of(130));
        assertThat(result.currentStock()).isEqualTo(StockQuantity.of(100));
    }

    @Test
    @DisplayName("getProducyByName(): 存在しない商品名を指定した場合、NotFoundExceptionをスローする")
    void getProductByName_notFound() {
        var name = ProductName.of("ペーパーナイフ");

        assertThatThrownBy(() -> service.getProducyByName(name))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("商品名:[ペーパーナイフ]の商品は存在しません。");
    }

}

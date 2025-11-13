package com.example.ddd_demo.application.product.usecase.interractor;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.dto.StockDTO;
import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.product.usecase.interactor.RegisterProductInteractor;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductRepository;

/**
 * ユースケース:[商品を登録する]のカテゴリ関連機能のテストドライバ
 */
@SpringBootTest
@Transactional
public class RegisterProductInteractorTest {
    /**
     * テストターゲット
     */
    @Autowired
    private RegisterProductInteractor interactor;
    /**
     * データ取得で利用
     */
    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("getCategories(): 登録済みカテゴリをすべて取得できる")
    void getCategories_success() {
        List<CategoryDTO> categories = interactor.getCategories();

        // 空でないことを検証する
        assertThat(categories).isNotEmpty();
        // 取得した商品カテゴリ名を検証する
        assertThat(categories)
            .extracting(CategoryDTO::getName)
            .contains("文房具", "雑貨", "パソコン周辺機器");
    }

    @Test
    @DisplayName("getCategoryById(): 既存カテゴリIdを指定して取得できる")
    void getCategoryById_success() {
        String stationeryId = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";

        CategoryDTO dto = interactor.getCategoryById(stationeryId);
        // nullでないことを検証する
        assertThat(dto).isNotNull();
        // 商品カテゴリIdを検証する
        assertThat(dto.getId()).isEqualTo(stationeryId);
        // 商品カテゴリ名を検証する
        assertThat(dto.getName()).isEqualTo("文房具");
    }

    @Test
    @DisplayName("getCategoryById(): 存在しないカテゴリIdならはNotFoundExceptionをスローする")
    void getCategoryById_notFound() {
        String unknownId = UUID.randomUUID().toString();
        // NotFoundExceptionがスローされたことを検証する
        assertThatThrownBy(() -> interactor.getCategoryById(unknownId))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(String.format(
                "商品カテゴリId:[%s]の商品カテゴリは存在しません。", unknownId));
    }

    @Test
    @DisplayName("existsProduct(): 既存の商品名ならExistsExceptionをスローする")
    void existsProduct_whenExists_thenThrows() {
        String existingName = "蛍光ペン(赤)";
        // ExistsExceptionがスローされたことを検証する
        assertThatThrownBy(() -> interactor.existsProduct(existingName))
            .isInstanceOf(ExistsException.class)
            .hasMessageContaining("商品名:[蛍光ペン(赤)]は既に登録済みです。");
    }

    @Test
    @DisplayName("existsProduct(): 未登録の商品名ならExistsExceptionはスローされない")
    void existsProduct_whenNotExists_thenNoThrow() {
        String notRegistered = "テスト未登録商品XYZ";
        // 例外がスローされないことを検証する
        assertThatCode(() -> interactor.existsProduct(notRegistered))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addProduct(): 新規商品を登録でき、商品と商品在庫が作成される")
    void addProduct_success() {
        // 登録データを用意する
        ProductDTO dto = new ProductDTO(
            null,                          
            "ペーパーナイフ",                
            1200,                           
            new CategoryDTO("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具"), 
            new StockDTO(null, 15)         
        );
        // 登録する
        interactor.addProduct(dto);
        var product = repository.findByName(ProductName.of("ペーパーナイフ"));
        // 検索結果が存在することを検証する
        assertThat(product).isPresent();
        // 検索結果を取り出す
        Product p = product.get();
        // 商品名を検証する
        assertThat(p.getName().value()).isEqualTo("ペーパーナイフ");
        // 単価を検証する
        assertThat(p.getPrice().value()).isEqualTo(1200);
        // 商品カテゴリIdを検証する
        assertThat(p.getCategory().getCategoryId().value())
            .isEqualTo("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
        // 在庫数を検証する
        assertThat(p.currentStock().value()).isEqualTo(15);
    }

}

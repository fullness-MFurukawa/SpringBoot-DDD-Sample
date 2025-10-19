package com.example.ddd_demo.application.service.product;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;

import lombok.RequiredArgsConstructor;

/**
 * 商品名検索サービスインターフェイスの実装のテストドライバ
 */
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SearchProductByNameServiceImplTest {

    /**
     * テストターゲット
     */
    @Autowired
    private SearchProductByNameService service; 

    @Test
    @DisplayName("search(): 登録済みの商品名でProductを取得できる")
    void search_found() {
        Product product = service.search(ProductName.of("蛍光ペン(緑)"));
        // 商品Idを検証する
        assertThat(product.getProductId().value()).isEqualTo("35cb51a7-df79-4771-9939-7f32c19bca45");
        // 商品名を検証する
        assertThat(product.getName().value()).isEqualTo("蛍光ペン(緑)");
        // 商品単価を検証する
        assertThat(product.getPrice().value()).isEqualTo(130);
        // 商品在庫を検証する
        assertThat(product.currentStock().value()).isEqualTo(100);
    }

    @Test
    @DisplayName("search(): 存在しない商品名は NotFoundException")
    void search_notFound_throws() {
        assertThatThrownBy(() -> service.search(ProductName.of("ABC")))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("商品名:[ABC]の商品は存在しません。");
    }
}

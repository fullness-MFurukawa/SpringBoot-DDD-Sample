package com.example.ddd_demo.application.usecase.mapper;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.application.usecase.dto.StockDTO;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * MapStruct版:ProductエンティティとProductDTOの相互変換Mapperの単体テストドライバ
 */
@SpringBootTest
public class ProductMapperTest {
    /**
     * テストターゲット
     */
    @Autowired
    private ProductMapper mapper;

    private static final String PRODUCT_UUID  = "83fbc81d-2498-4da6-b8c2-54878d3b67ff";
    private static final String CATEGORY_UUID = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";
    private static final String STOCK_UUID    = "b9e5d6fb-0a3a-4a9a-9f0b-0b2d5c8d9c3a";

    @Nested
    class 正常系 {

        @Test
        @DisplayName("toDomain(): 商品ID未指定なら新規作成として組み立てる")
        void toDomain_createNew() {
            var dto = new ProductDTO(
                null,                             // product id 未指定
                "万年筆",                           // name
                1200,                             // price
                new CategoryDTO(CATEGORY_UUID, "文房具"),
                new StockDTO(null, 10)            // stock id 未指定 → 新規在庫
            );

            Product product = mapper.toDomain(dto);

            assertThat(product.getName()).isEqualTo(ProductName.of("万年筆"));
            assertThat(product.getPrice()).isEqualTo(ProductPrice.of(1200));
            assertThat(product.getCategoryId().value()).isEqualTo(CATEGORY_UUID);
            assertThat(product.currentStock()).isEqualTo(StockQuantity.of(10));
            // 新規作成なので productId は新規UUID（形式チェック）
            assertThat(product.getProductId().value()).matches("^[0-9a-f\\-]{36}$");
        }

        @Test
        @DisplayName("toDomain(): 商品ID指定ありなら再構築として組み立てる")
        void toDomain_rehydrate() {
            var dto = new ProductDTO(
                PRODUCT_UUID,
                "消しゴム",
                100,
                new CategoryDTO(CATEGORY_UUID, "文房具"),
                new StockDTO(STOCK_UUID, 5)
            );

            Product product = mapper.toDomain(dto);

            assertThat(product.getProductId().value()).isEqualTo(PRODUCT_UUID);
            assertThat(product.getName()).isEqualTo(ProductName.of("消しゴム"));
            assertThat(product.getPrice()).isEqualTo(ProductPrice.of(100));
            assertThat(product.getCategoryId().value()).isEqualTo(CATEGORY_UUID);
            assertThat(product.currentStock()).isEqualTo(StockQuantity.of(5));
        }

        @Test
        @DisplayName("fromDomain(): Product を ProductDTO に正しく変換する")
        void toDto_success() {
            var domain = Product.rehydrate(
                ProductId.fromString(PRODUCT_UUID),
                ProductName.of("蛍光ペン(赤)"),
                ProductPrice.of(130),
                CategoryId.fromString(CATEGORY_UUID),
                Stock.rehydrate(StockId.fromString(STOCK_UUID), StockQuantity.of(100))
            );

            var dto = mapper.fromDomain(domain);

            assertThat(dto.getId()).isEqualTo(PRODUCT_UUID);
            assertThat(dto.getName()).isEqualTo("蛍光ペン(赤)");
            assertThat(dto.getPrice()).isEqualTo(130);

            assertThat(dto.getCategory()).isNotNull();
            assertThat(dto.getCategory().getId()).isEqualTo(CATEGORY_UUID);

            assertThat(dto.getStock()).isNotNull();
            assertThat(dto.getStock().getId()).isEqualTo(STOCK_UUID);
            assertThat(dto.getStock().getQuantity()).isEqualTo(100);
        }
    }

}

package com.example.ddd_demo.application.mapper;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.exception.InvalidInputException;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;

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

    @Nested
    class 正常系 {
        @Test
        @DisplayName("toDomain(): ID未指定なら新規発番としてProductを再構築する")
        void toDomain_createNew_when_id_null() {
            var dto = new ProductDTO(
                null,                 // ← IDなし
                "蛍光ペン(黄)",
                130,
                null,
                null
            );
            Product result = mapper.toDomain(dto);
            assertThat(result).isNotNull();
            assertThat(result.getProductId().value())
                .as("新規発番されたUUID（canonical形式）であること")
                .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
            assertThat(result.getName().value()).isEqualTo("蛍光ペン(黄)");
            assertThat(result.getPrice().value()).isEqualTo(130);
        }

        @Test
        @DisplayName("toDomain():ProductID指定ありなら同一IDのProductを再構築できる")
        void toDomain_rehydrate_with_id() {
            var id = "83fbc81d-2498-4da6-b8c2-54878d3b67ff";
            var dto = new ProductDTO(
                id,
                "えんぴつ",
                120,
                null,
                null
            );
            Product result = mapper.toDomain(dto);
            assertThat(result).isNotNull();
            assertThat(result.getProductId().value()).isEqualTo(id);
            assertThat(result.getName().value()).isEqualTo("えんぴつ");
            assertThat(result.getPrice().value()).isEqualTo(120);
        }

        @Test
        @DisplayName("fromDomain(): DomainからDTOへ基本項目(id/name/price)を正しく変換できる")
        void fromDomain_success() {
            Product domain = Product.restoreSkeleton(
                ProductId.fromString("ac413f22-0cf1-490a-9635-7e9ca810e544"),
                ProductName.of("無線式キーボード"),
                ProductPrice.of(1900)
            );

            ProductDTO dto = mapper.fromDomain(domain);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo("ac413f22-0cf1-490a-9635-7e9ca810e544");
            assertThat(dto.getName()).isEqualTo("無線式キーボード");
            assertThat(dto.getPrice()).isEqualTo(1900);
            assertThat(dto.getCategory()).isNull();
            assertThat(dto.getStock()).isNull();
        }

    }

    @Nested
    class 異常系 {
        @Test
        @DisplayName("toDomain(): dtoがnullならInvalidInputExceptionをスローする")
        void toDomain_null_throws() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("ProductDTOがnullです。");
        }

        @Test
        @DisplayName("toDomain(): nameが空白のみならInvalidInputExceptionをスローする")
        void toDomain_blank_name_throws() {
            var dto = new ProductDTO(
                null,
                "   ",
                120,
                null,
                null
            );

            assertThatThrownBy(() -> mapper.toDomain(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("商品名は必須です。");
        }

        @Test
        @DisplayName("toDomain(): priceがnullならInvalidInputExceptionをスロー")
        void toDomain_null_price_throws() {
            var dto = new ProductDTO(
                null,
                "消しゴム",
                null,
                null,
                null
            );

            assertThatThrownBy(() -> mapper.toDomain(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("商品単価は必須です。");
        }

        @Test
        @DisplayName("toDomain(): 不正なID形式ならDomainExceptionをスローする")
        void toDomain_invalid_id_throws() {
            var dto = new ProductDTO(
                "not-a-uuid",
                "定規",
                200,
                null,
                null
            );

            assertThatThrownBy(() -> mapper.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UUID");
        }

        @Test
        @DisplayName("toDomain(): 単価が下限未満(50未満)ならDomainExceptionをスローする")
        void toDomain_price_tooLow_throws() {
            var dto = new ProductDTO(
                null,
                "消しゴム",
                49,  // 下限違反
                null,
                null
            );

            assertThatThrownBy(() -> mapper.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品単価");
        }

        @Test
        @DisplayName("toDomain(): 単価が上限超過(>10000)ならDomainExceptionをスローする")
        void toDomain_price_tooHigh_throws() {
            var dto = new ProductDTO(
                null,
                "定規",
                10001,  // 上限違反
                null,
                null
            );

            assertThatThrownBy(() -> mapper.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品単価");
        }

        @Test
        @DisplayName("fromDomain(): domainがnullならInvalidInputExceptionをスロー")
        void fromDomain_null_throws() {
            assertThatThrownBy(() -> mapper.fromDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Productがnullです。");
        }
    }

}

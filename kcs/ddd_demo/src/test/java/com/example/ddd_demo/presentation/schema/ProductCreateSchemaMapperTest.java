package com.example.ddd_demo.presentation.schema;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.presentation.product.schema.ProductCreateSchema;
import com.example.ddd_demo.presentation.product.schema.ProductCreateSchemaMapper;

/**
 * ProductCreateSchemaからProductDTOへの変換Mapperのテストドライバ
 */
@SpringBootTest
public class ProductCreateSchemaMapperTest {
    /**
     * テストターゲット
     */
    @Autowired
    private ProductCreateSchemaMapper mapper;

    @Test
    @DisplayName("toDto(): ProductCreateSchemaからProductDTOへ正常にマッピングできる")
    void toDto_success() {
        // データを用意する
        var schema = new ProductCreateSchema(
                "筆ペン",
                300,
                "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4",
                10
        );
        // ProductDTOに変換する
        ProductDTO dto = mapper.toDto(schema);
        // 結果を検証する
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull(); // 新規作成なのでnull
        assertThat(dto.getName()).isEqualTo("筆ペン");
        assertThat(dto.getPrice()).isEqualTo(300);
        assertThat(dto.getCategory()).isNotNull();
        assertThat(dto.getCategory().getId()).isEqualTo("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
        assertThat(dto.getCategory().getName()).isNull();
        assertThat(dto.getStock()).isNotNull();
        assertThat(dto.getStock().getId()).isNull();
        assertThat(dto.getStock().getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("toDto(): nullを渡した場合はnullを返す")
    void toDto_nullInput_returnsNull() {
        // nullでProductDTOを変換する
        ProductDTO dto = mapper.toDto(null);
        // nullであることを検証する
        assertThat(dto).isNull();
    }
}   

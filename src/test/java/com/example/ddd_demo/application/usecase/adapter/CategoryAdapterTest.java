package com.example.ddd_demo.application.usecase.adapter;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;

/**
 * CategoryエンティティとCategoryDTOの相互変換Adapterのテストドライバ
 */
@Deprecated(since = "2025-10-26", forRemoval = true)
@SpringBootTest
public class CategoryAdapterTest {
    /**
     * テストターゲット
     */
    @Autowired
    private CategoryAdapter adapter;

    @Nested
    class 正常系 {

        @Test
        @DisplayName("toDomain(): ID未指定(null/空白)なら新規作成として扱う")
        void toDomain_createNew_when_id_blank() {
            var dto = new CategoryDTO(null, "文房具");

            var result = adapter.toDomain(dto);

            assertThat(result.getName().value()).isEqualTo("文房具");
            assertThat(result.getCategoryId().value())
                .as("新規発番されたUUID（canonical形式）であること")
                .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("toDomain(): ID指定ありなら再構築として扱う")
        void toDomain_rehydrate() {
            var id = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";
            var dto = new CategoryDTO(id, "文房具");

            var result = adapter.toDomain(dto);

            assertThat(result.getCategoryId().value()).isEqualTo(id);
            assertThat(result.getName().value()).isEqualTo("文房具");
        }

        @Test
        @DisplayName("fromDomain(): 正しくDTOへ変換できる")
        void fromDomain_success() {
            var id = CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
            var name = CategoryName.of("雑貨");
            var domain = Category.rehydrate(id, name);

            var dto = adapter.fromDomain(domain);

            assertThat(dto.getId()).isEqualTo(id.value());
            assertThat(dto.getName()).isEqualTo("雑貨");
        }
    }

    @Nested
    class 異常系 {

        @Test
        @DisplayName("toDomain(): 入力がnullならInvalidInputExceptionをスローする")
        void toDomain_null_throws() {
            assertThatThrownBy(() -> adapter.toDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("CategoryDTOがnullです。");
        }

        @Test
        @DisplayName("fromDomain(): 入力がnullならInvalidInputExceptionをスローする")
        void fromDomain_null_throws() {
            assertThatThrownBy(() -> adapter.fromDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Categoryがnullです。");
        }

        @Test
        @DisplayName("toDomain(): nameが空白のみならDomainExceptionをスローする")
        void toDomain_blank_name_throws() {
            var dto = new CategoryDTO(null, "   ");
            assertThatThrownBy(() -> adapter.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("カテゴリ名");
        }

        @Test
        @DisplayName("toDomain(): 不正なID形式ならDomainExceptionをスローする")
        void toDomain_invalid_id_throws() {
            var dto = new CategoryDTO("not-a-uuid", "パソコン周辺機器");
            assertThatThrownBy(() -> adapter.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UUID");
        }
    }
}

package com.example.ddd_demo.application.category.service.impl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.category.service.CategoryService;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;

/**
 * 商品カテゴリサービスインターフェイス実装テストドライバ
 */
@SpringBootTest
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService service;

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
}

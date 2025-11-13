package com.example.ddd_demo.infrastructure.persistence.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;

/**
 * CategoryRepositoryインターフェイス実装のテストドライバ
 */
@SpringBootTest
public class CategoryJooqRepositoryTest {
  
    /**
     * テストターゲット
     */
    @Autowired
    private CategoryJooqRepository repository;

    @Test
    @DisplayName("findById(): カテゴリが存在すれば取得できる")
    void testFindById_found() {
        UUID uuid = UUID.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
        Optional<Category> found = repository.findById(CategoryId.fromString(uuid.toString()));

        assertThat(found).isPresent();
        assertThat(found.get().getName().value()).isEqualTo("文房具");
    }

    @Test
    @DisplayName("findById(): 存在しないカテゴリIDならemptyを返す")
    void testFindById_notFound() {
        Optional<Category> found = repository.findById(
            CategoryId.fromString("8f81a72a-58ef-422b-b472-d982e8665292")
        );
        assertThat(found).isEmpty();
    }
}

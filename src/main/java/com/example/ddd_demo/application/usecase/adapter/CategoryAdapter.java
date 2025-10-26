package com.example.ddd_demo.application.usecase.adapter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.domain.adapter.DomainBiAdapter;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;

/**
 * CategoryエンティティとCategoryDTOの相互変換Adapter
 * <p>⚠️ MapStruct版 {@code ProductRecordMapper} を導入したため、このクラスは非推奨です。</p>
 * <p>将来的には削除予定です。</p>
 */
@Deprecated(since = "2025-10-26", forRemoval = true)
@Component
public class CategoryAdapter implements DomainBiAdapter<CategoryDTO , Category> {
    /**
     * CategoryDTOからCategoryエンティティを再構築する
     * @param input CategoryDTO
     * @return Category
     */
    @Override
    public Category toDomain(CategoryDTO input) {
        if (input == null){
            throw new InvalidInputException("CategoryDTOがnullです。");
        }

        String rawId = input.getId();
        String rawName = input.getName();
        String id = (rawId == null) ? null : rawId.trim();
        String name = (rawName == null) ? null : rawName.trim();

        if (!StringUtils.hasText(id)) {
            // 新規作成（IDは未指定）
            return Category.createNew(CategoryName.of(name));
        } else {
            // 既存再構築
            return Category.rehydrate(
                CategoryId.fromString(id),
                CategoryName.of(name)
            );
        }
    }

    /**
     * CategoryエンティティをCategoryDTOに変換する
     * @param domain Category
     * @return CategoryDTO
     */
    @Override
    public CategoryDTO fromDomain(Category domain) {
        if (domain == null){
            throw new InvalidInputException("Categoryがnullです。");
        }
        return new CategoryDTO(domain.getCategoryId().value(),
                                domain.getName().value());
    }
}

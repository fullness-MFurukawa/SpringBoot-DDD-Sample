package com.example.ddd_demo.infrastructure.persistence.category;

import java.util.UUID;

import org.jooq.Record2;
import org.mapstruct.Mapper;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductCategoryTable;

/**
 * jOOQのRecordからCategoryエンティティを再構築するMapper
 */
@Mapper(componentModel = "spring")
public interface CategoryRecordMapper {
    /**
     * jOOQのRecordからCategoryエンティティを再構築する
     * @param input jOOQのRecord2<UUID, String>
     * @return 再構築されたCategory
     */
    default Category toDomain(Record2<UUID, String> input) {
        if (input == null) throw new DomainException("カテゴリ情報が取得できません。");

        String uuid = String.valueOf(input.get(ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID));
        String name = input.get(ProductCategoryTable.PRODUCT_CATEGORY.NAME);

        if (uuid == null || uuid.isBlank()) throw new DomainException("カテゴリUUIDが不正です。");
        if (name == null || name.isBlank()) throw new DomainException("カテゴリ名が未設定です。");

        return Category.rehydrate(CategoryId.fromString(uuid), CategoryName.of(name));
    }
}

package com.example.ddd_demo.application.usecase.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;

/**
 * CategoryエンティティとCategoryDTOの相互変換Mapper
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends DomainBiMapper<CategoryDTO, Category>{

    /**
     * CategoryDTOからCategoryエンティティを再構築する
     * @param dto  CategoryDTO
     * @return Category
     */
    default Category toDomain(CategoryDTO dto) {
        if (dto == null) 
            throw new InvalidInputException("CategoryDTOがnullです。");
        if (!StringUtils.hasText(dto.getName())) 
            throw new InvalidInputException("商品カテゴリ名は必須です。");

        if (!StringUtils.hasText(dto.getId())) {
            // 新規作成
            return Category.createNew(CategoryName.of(dto.getName()));
        } else {
            // 再構築
            return Category.rehydrate(CategoryId.fromString(dto.getId()), CategoryName.of(dto.getName()));
        }
    }

    /**
     * CategoryエンティティをCategoryDTOに変換する
     * @param domain Category
     * @return CategoryDTO
     */
    default CategoryDTO fromDomain(Category domain) {
        if (domain == null) 
            throw new InvalidInputException("Categoryがnullです。");
        return new CategoryDTO(domain.getCategoryId().value(), domain.getName().value());
    }
}

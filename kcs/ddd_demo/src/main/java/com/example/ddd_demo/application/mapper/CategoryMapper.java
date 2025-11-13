package com.example.ddd_demo.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;

/**
 * {@link Category} エンティティと {@link CategoryDTO} の
 * 相互変換を行うアプリケーション層Mapper。
 *
 * <p>責務：</p>
 * <ul>
 *   <li>DTO → Entity 変換時の入力検証（Null／必須値）</li>
 *   <li>Entity → DTO 変換時のドメイン情報抽出</li>
 * </ul>
 *
 * <p>非責務：</p>
 * <ul>
 *   <li>永続化やDBアクセス（Repository側の責務）</li>
 *   <li>ドメインルールの実行（Entity/ValueObject側の責務）</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends DomainBiMapper<CategoryDTO, Category>{

    /**
     * CategoryDTO から Category エンティティを再構築する。
     *
     * <p>ID未指定（新規作成）の場合は {@link Category#createNew(CategoryName)} を呼び出し、
     * ID指定済み（再構築）の場合は {@link Category#restore(CategoryId, CategoryName)} を呼び出す。</p>
     *
     * @param dto CategoryDTO
     * @return Category エンティティ
     * @throws InvalidInputException DTOの必須項目が欠落している場合
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
            return Category.restore(CategoryId.fromString(dto.getId()), CategoryName.of(dto.getName()));
        }
    }

    /**
     * Category エンティティを CategoryDTO に変換する。
     *
     * @param domain Category エンティティ
     * @return CategoryDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    default CategoryDTO fromDomain(Category domain) {
        if (domain == null) 
            throw new InvalidInputException("Categoryがnullです。");
        return new CategoryDTO(domain.getCategoryId().value(), domain.getName().value());
    }
}

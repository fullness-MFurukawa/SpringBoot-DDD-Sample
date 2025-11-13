package com.example.ddd_demo.presentation.product.schema;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.dto.StockDTO;

/**
 * {@code ProductCreateSchemaMapper} は、
 * プレゼンテーション層（リクエストスキーマ）から
 * アプリケーション層の {@link ProductDTO} へ変換するためのACL（腐敗防止層）アダプタです。
 */
@Mapper(
    componentModel = "spring",
    imports = { CategoryDTO.class, StockDTO.class }
)
public interface ProductCreateSchemaMapper {
    @Mappings({
        // 新規作成なのでidは無視
        @Mapping(target = "id", ignore = true),
        // ネストDTOはJava式で生成
        @Mapping(target = "category",
                 expression = "java(new CategoryDTO(schema.categoryId(), null))"),
        @Mapping(target = "stock",
                 expression = "java(new StockDTO(null, schema.stockQuantity()))")
    })
    ProductDTO toDto(ProductCreateSchema schema);
}

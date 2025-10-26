package com.example.ddd_demo.presentation.schema;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.application.usecase.dto.StockDTO;

/**
 * ProductCreateSchemaからProductDTOへの変換Mapper
 */
@Mapper(
    componentModel = "spring",
    imports = { CategoryDTO.class, StockDTO.class }
)
public interface ProductCreateSchemaMapper {
    @Mappings({
        // 新規作成なので id は無視
        @Mapping(target = "id", ignore = true),
        // ネストDTOはJava式で生成
        @Mapping(target = "category",
                 expression = "java(new CategoryDTO(schema.categoryId(), null))"),
        @Mapping(target = "stock",
                 expression = "java(new StockDTO(null, schema.stockQuantity()))")
    })
    ProductDTO toDto(ProductCreateSchema schema);
}

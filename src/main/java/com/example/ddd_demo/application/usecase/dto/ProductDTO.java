package com.example.ddd_demo.application.usecase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品DTO
 */
@Schema(name = "Product", description = "商品DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    /**
     * 商品Id
     */
    @Schema(description = "商品ID(UUID)", example = "83fbc81d-2498-4da6-b8c2-54878d3b67ff")
    private String  id;
    /**
     * 商品名
     */
    @Schema(description = "商品名", example = "蛍光ペン(赤)")
    private String  name;
    /**
     * 商品単価
     */
    @Schema(description = "商品単価（円）", example = "130")    
    private Integer price;

    /**
     * 商品カテゴリ
     */
    @Schema(description = "商品カテゴリ情報")
    private CategoryDTO category;

    /**
     * 商品在庫
     */
    @Schema(description = "商品在庫情報")
    private StockDTO stock;
}

package com.example.ddd_demo.application.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品在庫DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    /**
     * 商品在庫Id
     */
    private String Id;
    /**
     * 商品在庫数
     */
    private Integer quantity;
}

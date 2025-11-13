package com.example.ddd_demo.application.dto;

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
     * 在庫ID(UUID形式)
     * {@code StockId} 値オブジェクトに対応。 
     */
    private String Id;
    /** 
     * 在庫数量
     * {@code StockQuantity} 値オブジェクトに対応。 
     */
    private Integer quantity;
}

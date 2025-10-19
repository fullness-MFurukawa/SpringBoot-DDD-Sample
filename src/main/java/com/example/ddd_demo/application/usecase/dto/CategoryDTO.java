package com.example.ddd_demo.application.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    /**
     * 商品カテゴリId
     */
    private String  id;
    /**
     * 商品カテゴリ名
     */
    private String  name;
}

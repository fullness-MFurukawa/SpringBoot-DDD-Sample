package com.example.ddd_demo.application.product.usecase;

import com.example.ddd_demo.application.dto.ProductDTO;

/**
 * ユースケース:[商品を名前で検索する]を実現するインターフェイス
 */
public interface SearchProductByNameUsecase {
    /**
     * 商品名を指定して商品情報を取得する
     * @param name 商品名
     * @return 存在する場合 ProductDTO
     */
    ProductDTO search(String name);
}

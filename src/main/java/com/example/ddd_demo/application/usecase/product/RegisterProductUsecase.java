package com.example.ddd_demo.application.usecase.product;

import java.util.List;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;

/**
 * ユースケース:[商品を登録する]を実現するインターフェイス
 */
public interface RegisterProductUsecase {
    /**
     * すべての商品カテゴリを取得する
     * @return 商品カテゴリリスト
     */
    List<CategoryDTO> getCategories();

    /**
     * 商品カテゴリIdで商品カテゴリを取得する
     * @param categoryId 商品カテゴリId
     * @return 商品カテゴリ
     */
    CategoryDTO getCategoryById(String categoryId);

    /**
     * 商品が既に存在するかを調べる
     * @param productName 商品名(
     */
    void existsProduct(String productName);

    /**
     * 商品を登録する
     * @param product 登録対象商品
     * @return 登録後の商品DTO
     */
    ProductDTO addProduct(ProductDTO product);
}

package com.example.ddd_demo.application.service.product;

import java.util.List;

import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;

/**
 * 商品登録サービスインターフェイス
 */
public interface RegisterProductService {
    
    /**
     * すべての商品カテゴリを取得する
     * @return 商品カテゴリリスト
     */
    List<Category> getCategories();

    /**
     * 商品カテゴリIdで商品カテゴリを取得する
     * @param categoryId 商品カテゴリId(VO)
     * @return 商品カテゴリ
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品カテゴリIdに該当する商品カテゴリが存在しない場合
     */
    Category getCategoryById(CategoryId categoryId);

    /**
     * 商品が既に存在するかを調べる
     * @param productName 商品名(VO)
     * @throws com.example.ddd_demo.application.exception.ExistsException
     *         指定された商品名の商品が既に存在する場合     
     */
    void existsProduct(ProductName productName);

    /**
     * 商品名で商品を取得する
     * @param productName 商品名(VO)
     * @return 商品
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品名に該当する商品が存在しない場合
     */
    Product getProducyByName(ProductName productName);

    /**
     * 商品を登録する
     * @param product 登録対象商品(Entity)
     */
    void addProduct(Product product);
}

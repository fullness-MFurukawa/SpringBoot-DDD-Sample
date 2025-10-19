package com.example.ddd_demo.domain.models.product;

import java.util.Optional;

/**
 * 商品のリポジトリインターフェイス
 */
public interface ProductRepository {
    
    /**
     * 新しい商品を永続化する
     * @param product 永続化する商品
     */
    void create(Product product);

    /**
     * 指定された商品名が存在有無を返す
     * @param productName 商品名
     * @return true:存在する false:存在しない
     */
    Boolean existsByName(ProductName productName);

    /**
     * 指定された商品Idの商品を取得する
     * @param categoryId 商品Id(VO)
     * @return 
     *  - 存在する場合: Productエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    Optional<Product> findById(ProductId productId);

    /** 
     * 商品名で商品を取得する
     * @param productName 商品名（VO）
     *  - 存在する場合: Productエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    Optional<Product> findByName(ProductName productName);
}

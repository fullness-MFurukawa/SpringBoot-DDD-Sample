package com.example.ddd_demo.domain.models.category;

import java.util.List;
import java.util.Optional;

/**
 * 商品カテゴリのリポジトリインターフェイス
 */
public interface CategoryRepository {
    
    /**
     * 指定された商品カテゴリIdのカテゴリを取得する
     * @param categoryId 商品カテゴリId(VO)
     * @return 
     *  - 存在する場合: Categoryエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    Optional<Category> findById(CategoryId categoryId);

    /**
     * すべての商品カテゴリを取得する
     * @return すべての商品カテゴリを持つリスト
     */
    List<Category> findAll();
}

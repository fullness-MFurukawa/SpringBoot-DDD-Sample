package com.example.ddd_demo.application.service.product;

import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;

/**
 * 商品名検索サービスインターフェイス
 */
public interface SearchProductByNameService {
    /**
     * 商品名を指定して商品情報を取得する
     * @param name 商品名
     * @return 存在する場合 {@link Product} エンティティ
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品名に該当する商品が存在しない場合
     */
    Product search(ProductName name);
}

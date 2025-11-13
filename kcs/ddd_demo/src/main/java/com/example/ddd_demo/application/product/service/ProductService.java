package com.example.ddd_demo.application.product.service;

import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;

/**
 * 商品に関するアプリケーションサービスインターフェイス。
 *
 * <p>本インターフェイスは、ユースケースから呼び出される
 * ドメイン操作の窓口を定義する。</p>
 * 
 * <p>Serviceは単一のEntity（ここではProduct）に対して作成し、
 * 複数のUseCaseから共通的に利用される。</p>
 */
public interface ProductService {

    /**
     * 商品が既に存在するかを調べる
     * @param productName 商品名(VO)
     * @throws com.example.ddd_demo.application.exception.ExistsException
     *         指定された商品名の商品が既に存在する場合     
     */
    void existsProduct(ProductName productName);

    /**
     * 商品Idで商品を取得する
     * @param productId 商品Id(VO)
     * @return 商品
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品Idに該当する商品が存在しない場合
     */
    Product getProductById(ProductId productId);

    /**
     * 商品名で商品を取得する
     * @param productName 商品名(VO)
     * @return 商品
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品名に該当する商品が存在しない場合
     */
    Product getProductByName(ProductName productName);

    /**
     * 商品を登録する
     * @param product 登録対象商品(Entity)
     */
    void addProduct(Product product);
}

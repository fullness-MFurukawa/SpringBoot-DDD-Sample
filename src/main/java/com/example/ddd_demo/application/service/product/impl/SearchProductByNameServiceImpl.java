package com.example.ddd_demo.application.service.product.impl;

import org.springframework.stereotype.Service;

import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.service.product.SearchProductByNameService;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductRepository;

import lombok.RequiredArgsConstructor;
/**
 * 商品名検索サービスインターフェイスの実装
 */
@Service
@RequiredArgsConstructor
public class SearchProductByNameServiceImpl implements SearchProductByNameService{

    /**
     * 商品アクセスリポジトリインターフェイス
     */
    private final ProductRepository repository;

    /**
     * 商品名を指定して商品情報を取得する
     * @param name 商品名
     * @return 存在する場合 {@link Product} エンティティ
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品名に該当する商品が存在しない場合
     */
    @Override
    public Product search(ProductName name) {
        var result = repository.findByName(name)
            .orElseThrow(() -> 
            new NotFoundException(String.format(
                "商品名:[%s]の商品は存在しません。", name.value())));
        return result;
    }
}

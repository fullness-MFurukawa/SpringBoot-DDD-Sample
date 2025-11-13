package com.example.ddd_demo.application.product.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.product.service.ProductService;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductRepository;

import lombok.RequiredArgsConstructor;

/**
 * {@link ProductService} の実装クラス。
 *
 * <p>Repositoryを介してドメインモデルを操作し、
 * アプリケーション層の例外をスローする責務を担う。</p>
 * 
 * <p>Serviceはユースケースから呼び出され、
 * ドメイン層を抽象化したファサードとして振る舞う。</p>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    
    private final ProductRepository repository;

    /**
     * 商品が既に存在するかを調べる
     * @param productName 商品名(VO)
     * @throws com.example.ddd_demo.application.exception.ExistsException
     *         指定された商品名の商品が既に存在する場合     
     */
    @Override
    public void existsProduct(ProductName productName) {
        Optional.of(repository.existsByName(productName))
        .filter(exists -> !exists) // falseならOK、trueなら例外スロー
        .orElseThrow(() -> new ExistsException(
            String.format("商品名:[%s]は既に登録済みです。", productName.value())
        ));
    }

    /**
     * 商品Idで商品を取得する
     * @param productId 商品Id(VO)
     * @return 商品
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品Idに該当する商品が存在しない場合
     */
    @Override
    public Product getProductById(ProductId productId) {
        var result = repository.findById(productId)
            .orElseThrow(() -> 
            new NotFoundException(String.format(
                "商品Id:[%s]の商品は存在しません。", productId.value())));
        return result;
    }

    /**
     * 商品名で商品を取得する
     * @param productName 商品名(VO)
     * @return 商品
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品名に該当する商品が存在しない場合
     */
    @Override
    public Product getProductByName(ProductName productName) {
        var result = repository.findByName(productName)
            .orElseThrow(() -> 
            new NotFoundException(String.format(
                "商品名:[%s]の商品は存在しません。", productName.value())));
        return result;
    }

    /**
     * 商品を登録する
     * @param product 登録対象商品(Entity)
     */
    @Override
    public void addProduct(Product product) {
        repository.create(product);
    }

}

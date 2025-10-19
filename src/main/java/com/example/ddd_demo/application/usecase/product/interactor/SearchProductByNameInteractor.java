package com.example.ddd_demo.application.usecase.product.interactor;

import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.service.product.SearchProductByNameService;
import com.example.ddd_demo.application.usecase.annotation.UseCase;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.application.usecase.product.SearchProductByNameUsecase;
import com.example.ddd_demo.domain.adapter.DomainBiAdapter;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;

import lombok.RequiredArgsConstructor;

/**
 * ユースケース:[商品を名前で検索する]を実現するインターフェイスの実装
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchProductByNameInteractor implements SearchProductByNameUsecase{
    /**
     * 商品名検索サービスインターフェイス
     */
    private final SearchProductByNameService service;
    /**
     * ProductエンティティとProductDTOの相互変換Adapter
     */
    private final DomainBiAdapter<ProductDTO, Product> adapter;

    /**
     * 商品名を指定して商品情報を取得する
     * @param name 商品名
     * @return 存在する場合 ProductDTO
     */
    @Override
    public ProductDTO search(String name) {
        var result = service.search(ProductName.of(name));
        return adapter.fromDomain(result);
    }
}

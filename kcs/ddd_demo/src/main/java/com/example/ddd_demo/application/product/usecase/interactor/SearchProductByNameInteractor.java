package com.example.ddd_demo.application.product.usecase.interactor;

import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.annotation.UseCase;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.mapper.ProductDTOAssembler;
import com.example.ddd_demo.application.product.service.ProductService;
import com.example.ddd_demo.application.product.usecase.SearchProductByNameUsecase;
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
    private final ProductService service;
    /**
     * DmainEntityとDTOの相互変換と組み立て
     */
    private final ProductDTOAssembler assembler;


    /**
     * 商品名を指定して商品情報を取得する
     * @param name 商品名
     * @return 存在する場合 ProductDTO
     */
    @Override
    public ProductDTO search(String name) {
        // 名前で商品を検索
        var result = service.getProductByName(ProductName.of(name));
        // Productエンティティの集約をProductDTOの集約に変換して返す
        return assembler.assembleDto(result);
    }
}
    
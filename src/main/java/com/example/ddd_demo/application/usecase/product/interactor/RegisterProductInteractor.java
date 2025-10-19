package com.example.ddd_demo.application.usecase.product.interactor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.service.product.RegisterProductService;
import com.example.ddd_demo.application.usecase.annotation.UseCase;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.application.usecase.product.RegisterProductUsecase;
import com.example.ddd_demo.domain.adapter.DomainBiAdapter;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;

import lombok.RequiredArgsConstructor;

/**
 * ユースケース:[商品を登録する]を実現するインターフェイスの実装
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterProductInteractor implements RegisterProductUsecase{

    /**
     * 商品登録サービスインターフェイス
     */
    private final RegisterProductService service;
    /**
     * CategoryエンティティとCategoryDTOの相互変換Adapter
     */
    private final DomainBiAdapter<CategoryDTO , Category> categoryAdapter;
    /**
     * ProductエンティティとProductDTOの相互変換Adapter
     */
    private final DomainBiAdapter<ProductDTO, Product> productAdapter;
    
    /**
     * すべての商品カテゴリを取得する
     * @return 商品カテゴリリスト
     */
    @Override
    public List<CategoryDTO> getCategories() {
        var categories = new ArrayList<CategoryDTO>();
        var result = service.getCategories();
        result.forEach(c ->{
            categories.add(categoryAdapter.fromDomain(c));
        });
        return categories;
    }

    /**
     * 商品カテゴリIdで商品カテゴリを取得する
     * @param categoryId 商品カテゴリId
     * @return 商品カテゴリ
     */
    @Override
    public CategoryDTO getCategoryById(String categoryId) {
        var category = service.getCategoryById(CategoryId.fromString(categoryId));
        return categoryAdapter.fromDomain(category);
    }

    /**
     * 商品が既に存在するかを調べる
     * @param productName 商品名(
     */
    @Override
    public void existsProduct(String productName) {
        service.existsProduct(ProductName.of(productName));   
    }

    /**
     * 商品を登録する
     * @param product 登録対象商品
     */
    @Transactional
    @Override
    public ProductDTO addProduct(ProductDTO product) {
        // 商品カテゴリを取得する
        var category = service.getCategoryById(CategoryId.fromString(product.getCategory().getId()));
        // DTOに商品カテゴリ名を設定する
        product.getCategory().setName(category.getName().value());
        // ProductSTOからProductエンティティを復元する
        var entity = productAdapter.toDomain(product);
        // 商品を登録する
        service.addProduct(entity);
        // 登録した商品を取得する
        var newProduct = service.getProducyByName(ProductName.of(product.getName()));
        // ProductエンティティをProductDTOに変換して返す
        return productAdapter.fromDomain(newProduct);
    }
}

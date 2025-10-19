package com.example.ddd_demo.application.service.product.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.service.product.RegisterProductService;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryRepository;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductRepository;

import lombok.RequiredArgsConstructor;


/**
 * 商品登録サービスインターフェイスの実装
 */
@Service
@RequiredArgsConstructor
public class RegisterProductServiceImpl implements RegisterProductService{

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    
    /**
     * すべての商品カテゴリを取得する
     * @return 商品カテゴリリスト
     */
    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    /**
     * 商品カテゴリIdで商品カテゴリを取得する
     * @param categoryId 商品カテゴリId(VO)
     * @return 商品カテゴリ
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品カテゴリIdに該当する商品カテゴリが存在しない場合
     */
    @Override
    public Category getCategoryById(CategoryId categoryId) {
        var result = categoryRepository.findById(categoryId)
            .orElseThrow(() -> 
            new NotFoundException(String.format(
                "商品カテゴリId:[%s]の商品カテゴリは存在しません。", categoryId.value())));
        return result;
    }

    /**
     * 商品が既に存在するかを調べる
     * @param productName 商品名(VO)
     * @throws com.example.ddd_demo.application.exception.ExistsException
     *         指定された商品名の商品が既に存在する場合     
     */
    @Override
    public void existsProduct(ProductName productName) {
        if (productRepository.existsByName(productName)){
           throw new ExistsException(String.format(
                "商品名:[%s]は既に登録済みです。", productName.value())); 
        }
    }

    /**
     * 商品名で商品を取得する
     * @param productName 商品名(VO)
     * @return 商品
     * @throws com.example.ddd_demo.application.exception.NotFoundException
     *         指定された商品名に該当する商品が存在しない場合
     */
    @Override
    public Product getProducyByName(ProductName productName){
        var result = productRepository.findByName(productName)
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
       productRepository.create(product);
    }
}

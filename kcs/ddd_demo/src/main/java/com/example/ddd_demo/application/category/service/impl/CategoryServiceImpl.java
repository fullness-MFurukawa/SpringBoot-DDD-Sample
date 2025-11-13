package com.example.ddd_demo.application.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ddd_demo.application.category.service.CategoryService;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryRepository;

import lombok.RequiredArgsConstructor;

/**
 * {@link CategoryService} の実装クラス。
 * 
 * <p>アプリケーション層におけるカテゴリ取得機能を提供する。</p>
 * 
 * <p>Repositoryを介して永続層からドメインエンティティを取得し、
 * 必要に応じてアプリケーション例外に変換する。</p>
 * 
 * <p>ドメインの整合性検証やトランザクションは行わない。
 * （ユースケース層で管理される）</p>
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

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

}

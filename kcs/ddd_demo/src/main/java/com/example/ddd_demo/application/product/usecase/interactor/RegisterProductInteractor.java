package com.example.ddd_demo.application.product.usecase.interactor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.ddd_demo.application.annotation.UseCase;
import com.example.ddd_demo.application.category.service.CategoryService;
import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.mapper.ProductDTOAssembler;
import com.example.ddd_demo.application.product.service.ProductService;
import com.example.ddd_demo.application.product.usecase.RegisterProductUsecase;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.ProductName;

import lombok.RequiredArgsConstructor;

/**
 * <p><b>ユースケース実装(Interactor)</b>: {@link RegisterProductUsecase}</p>
 *
 * <h3>責務</h3>
 * <ul>
 *   <li>商品登録ユースケースのアプリケーション処理を統括する。</li>
 *   <li>ServiceとAssembler/Mapperを組み合わせ、DTOとドメインの橋渡しを行う。</li>
 *   <li>読み取り系は {@code @Transactional(readOnly = true)}、書き込み系はメソッドで {@code @Transactional} を付与しトランザクション境界を明確化する。</li>
 * </ul>
 *
 * <h3>非責務</h3>
 * <ul>
 *   <li>ビジネスルールの詳細（ドメイン層）</li>
 *   <li>永続化の詳細（インフラストラクチャ層）</li>
 * </ul>
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterProductInteractor implements RegisterProductUsecase{

    /**
     * 商品サービスインターフェイス
     */
    private final ProductService productService;
    /**
     * 商品カテゴリサービスインターフェイス
     */
    private final CategoryService categoryService;
    /**
     * DmainEntityとDTOの相互変換と組み立て
     */
    private final ProductDTOAssembler assembler;
    
    /**
     * {@inheritDoc}
     * <p>カテゴリ一覧をドメインから取得し、表示用DTOへ変換して返す。</p>
     */
    @Override
    public List<CategoryDTO> getCategories() {
        var categories = new ArrayList<CategoryDTO>();
        var result = categoryService.getCategories();
        result.forEach(c ->{
            categories.add(assembler.toCategoryDto(c));
        });
        return categories;
    }

    /**
     * {@inheritDoc}
     * <p>UUID文字列から {@link CategoryId} を再構築し、カテゴリを取得してDTOに変換する。</p>
     */
    @Override
    public CategoryDTO getCategoryById(String categoryId) {
        var category = categoryService.getCategoryById(CategoryId.fromString(categoryId));
        return assembler.toCategoryDto(category);
    }

    /**
     * {@inheritDoc}
     * <p>商品名のVO {@link ProductName} を生成して存在確認を行う。存在すれば例外が投げられる。</p>
     */
    @Override
    public void existsProduct(String productName) {
        productService.existsProduct(ProductName.of(productName));   
    }

    /**
     * {@inheritDoc}
     *
     * <h4>処理フロー</h4>
     * <ol>
     *   <li>DTOに含まれるカテゴリIDからカテゴリを取得（存在必須）。</li>
     *   <li>DTOへカテゴリ名等を再設定（整合性の明示）。</li>
     *   <li>AssemblerでDTO→ドメイン集約 {@code Product} を合成。</li>
     *   <li>アプリケーションサービス経由で登録。</li>
     *   <li>登録結果を商品名で再取得し、DTOに変換して返却。</li>
     * </ol>
     *
     * <h4>トランザクション</h4>
     * <p>本メソッドは書き込みを伴うため、メソッド境界で {@code @Transactional} を付与する。</p>
     */
    @Transactional
    @Override
    public ProductDTO addProduct(ProductDTO product) {
        // 商品カテゴリを取得する
        var category = categoryService.getCategoryById(CategoryId.fromString(product.getCategory().getId()));
        // DTOに商品カテゴリ名を設定する
        product.setCategory(assembler.toCategoryDto(category));
        // ProductSTOからProductエンティティを復元する
        var registProduct = assembler.assembleDomain(product);
        // 商品を登録する
        productService.addProduct(registProduct);
        // 登録した商品を取得する
        var newProduct = productService.getProductByName(ProductName.of(product.getName()));
        // ProductエンティティをProductDTOに変換して返す
        return assembler.assembleDto(newProduct);
    }
}

package com.example.ddd_demo.application.mapper;

import org.springframework.stereotype.Component;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.dto.StockDTO;
import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.stock.Stock;

import lombok.RequiredArgsConstructor;

/**
 * アプリケーション層のアセンブラ。
 *
 * <p>DTO群（ProductDTO / CategoryDTO / StockDTO）と
 * ドメイン集約 {@link Product} の合成／分解を担当する。</p>
 *
 * <p>Mapperを複合的に利用して、DTO構造とドメイン構造の
 * 対応関係を統一的に変換する責務を持つ。</p>
 *
 * <p>Assemblerは複数のMapperを統括し、DTO → Entity変換を「集約」単位で行う。</p>
 */
@Component
@RequiredArgsConstructor
public class ProductDTOAssembler {
    /**
     * ProductエンティティとProductDTOの相互変換Mapper
     */
    private final DomainBiMapper<ProductDTO, Product> productMapper;
    /**
     * CategoryエンティティとCategoryDTOの相互変換Mapper
     */
    private final DomainBiMapper<CategoryDTO, Category> categoryMapper;
    /**
     * StockエンティティとStockDTOの相互変換Mapper
     */
    private final DomainBiMapper<StockDTO, Stock> stockMapper;

    /**
     * DTO群からドメイン集約 {@link Product} を合成する。
     *
     * <p>カテゴリ／在庫DTOが揃っていない場合は例外をスロー。</p>
     *
     * @param product 入力DTO
     * @return 合成済み {@link Product}
     * @throws InvalidInputException 必須DTO欠落など不整合
     * @throws DomainException       値オブジェクトの検証失敗など
     */
    public Product assembleDomain(ProductDTO product) {
        if (product == null) 
            throw new InvalidInputException("ProductDTOがnullです。");
        if (product.getCategory() == null) 
            throw new InvalidInputException("CategoryDTOがnullです。");
        if (product.getStock() == null) 
            throw new InvalidInputException("StockDTOがnullです。");

        final Product skeleton = productMapper.toDomain(product); 
        final Category domainCategory = categoryMapper.toDomain(product.getCategory());
        final Stock domainStock = stockMapper.toDomain(product.getStock());

        return Product.restore(
            skeleton.getProductId(), 
            skeleton.getName(), 
            skeleton.getPrice(),
            domainCategory, 
            domainStock);
    }

    /**
     * ドメイン集約 {@link Product} をネストされたDTO構造に変換する。
     *
     * @param domain Productエンティティ
     * @return ProductDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    public ProductDTO assembleDto(Product domain) {
        if (domain == null) {
            throw new InvalidInputException("Productがnullです。");
        }
        // 基本項目(id/name/price)はProductMapperに委譲
        ProductDTO dto = productMapper.fromDomain(domain);
        // カテゴリ/在庫は存在する時のみDTO化
        if (domain.getCategory() != null) {
            dto.setCategory(categoryMapper.fromDomain(domain.getCategory()));
        }
        if (domain.getStock() != null) {
            dto.setStock(stockMapper.fromDomain(domain.getStock()));
        }
        return dto;
    }

    /**
     * Category エンティティを単独でDTO変換するユーティリティ。
     *
     * <p>ユースケース層などでカテゴリ一覧を返す際に利用する。</p>
     *
     * @param category Categoryエンティティ
     * @return CategoryDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    public CategoryDTO toCategoryDto(Category category) {
        if (category == null) {
            throw new InvalidInputException("Categoryがnullです。");
        }
        return categoryMapper.fromDomain(category);
    }

}

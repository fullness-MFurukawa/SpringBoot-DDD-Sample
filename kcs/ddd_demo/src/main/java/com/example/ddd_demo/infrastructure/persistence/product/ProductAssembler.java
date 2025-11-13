package com.example.ddd_demo.infrastructure.persistence.product;

import org.springframework.stereotype.Component;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.infrastructure.persistence.category.CategoryRecordMapper;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductCategoryRecord;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductRecord;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductStockRecord;
import com.example.ddd_demo.infrastructure.persistence.stock.StcokRecordMapper;

import lombok.RequiredArgsConstructor;

/**
 * Product 集約の「合成（Record → 集約）」および「個別のエンティティ変換」）」を担うアセンブラ。
 *
 * <p>責務分離の方針：</p>
 * <ul>
 *   <li>本クラスは<b>型変換と合成/分解</b>のみを担当</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ProductAssembler {
    /** 
     * ProductRecord <-> Product 
     */
    private final ProductRecordMapper productRecordMapper;
    /** 
     * ProductCategoryRecord -> Category 
     */
    private final CategoryRecordMapper categoryRecordMapper;
    /** 
     * ProductStockRecord<-> Stock 
     */
    private final StcokRecordMapper stockRecordMapper;

    // ----------------------------------------------------------------------
    // 合成（Record → 集約）
    // ----------------------------------------------------------------------
    /**
     * jOOQ 生成レコード3種から完全な {@link Product} を合成する。
     *
     * <p>Repository で JOIN して取得した各レコードを渡すと、
     * Product の再構築（rehydrate）を行う。</p>
     *
     * @param pr Productの基本情報（product_uuid, name, price）
     * @param cr Categoryの基本情報（category_uuid, name）
     * @param sr Stockの基本情報（stock_uuid, stock）
     * @return 合成済みのProduct集約
     * @throws DomainException 必須項目欠落や不正値の場合
     */
    public Product assemble(ProductRecord pr, ProductCategoryRecord cr, ProductStockRecord sr) {
        if (pr == null) throw new DomainException("ProductRecordがnullです。");
        if (cr == null) throw new DomainException("ProductCategoryRecordがnullです。");
        if (sr == null) throw new DomainException("ProductStockRecord がnullです。");
        // ProductRecordからProductを復元
        var product = productRecordMapper.toDomain(pr); 
        // ProductCategoryRecordからCategoryを復元
        var category = categoryRecordMapper.toDomain(cr);
        // ProductStockRecordからStockを復元
        var stock    = stockRecordMapper.toDomain(sr);
        // Product、Category、Stockを合成
        return Product.restore(
            product.getProductId(), 
            product.getName(), 
            product.getPrice(), 
            category, 
            stock);
    }

    /**
     * 集約からProductRecordを作る(INSERT/UPDATE 用)
     * 注意：category_id(外部キー)はここでは埋めない。Repositoryで補完する。
     */
    public ProductRecord toProductRecord(Product product) {
        if (product == null) throw new DomainException("Productがnullです。");
        return productRecordMapper.fromDomain(product);
    }

    /**
     * 集約からProductStockRecordを作る(INSERT/UPDATE 用)
     * 注意：product_id(外部キー)はここでは埋めない。Repositoryで補完する。
     */
    public ProductStockRecord toStockRecord(Product product) {
        if (product == null) throw new DomainException("Productがnullです。");
        return stockRecordMapper.fromDomain(product.getStock());
    }

    /**
     * 集約からCategoryのUUID(文字列)を取り出すユーティリティ。
     * Repositoryでcategory_id(外部キー))を解決するために利用。
     */
    public String extractCategoryUuid(Product product) {
        if (product == null) throw new DomainException("Productがnullです。");
        var category = product.getCategory();
        if (category == null) throw new DomainException("ProductにCategoryが設定されていません。");
        return category.getCategoryId().value();
    }

}   

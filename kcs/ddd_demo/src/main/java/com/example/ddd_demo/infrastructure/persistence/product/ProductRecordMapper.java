package com.example.ddd_demo.infrastructure.persistence.product;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductRecord;

/**
 * jOOQ が生成した {@link ProductRecord} と
 * ドメインエンティティ {@link Product} を相互変換する Mapper インターフェイス。
 *
 * <p>このクラスは DDD における「腐敗防止層（Anti-Corruption Layer）」として機能し、
 * 永続化層（jOOQ の Record 構造）とドメイン層（不変なエンティティ構造）を明確に分離します。</p>
 *
 * <p><b>責務：</b><br>
 * <ul>
 *   <li>DB の Product テーブル行（{@code ProductRecord}）を、
 *       ドメインの {@code Product} に再構築する（toDomain）。</li>
 *   <li>ドメインの {@code Product} を DB への保存用 {@code ProductRecord} に変換する（fromDomain）。</li>
 *   <li>在庫情報（{@link Stock}）は別 Mapper（例：StockRecordMapper）に委譲し、
 *       必要に応じてリポジトリ層で合成する。</li>
 * </ul>
 * </p>
 *
 * <p>MapStruct により実装クラス（{@code ProductRecordMapperImpl}）が自動生成され、
 * Spring 管理下の Bean として利用できます。</p>
 *
 * @see Product
 * @see ProductRecord
 * @see com.example.ddd_demo.domain.mapper.DomainBiMapper
 */
@Mapper(componentModel = "spring") // Spring管理Beanとして実装を生成する
public interface ProductRecordMapper extends DomainBiMapper<ProductRecord, Product> {
     /**
     * jOOQの{@link ProductRecord}からエンティティ {@link Product} を再構築する。
     * <p><b>主な変換処理：</b></p>
     * <ul>
     *   <li>{@code product_uuid} → {@link ProductId}</li>
     *   <li>{@code name} → {@link ProductName}</li>
     *   <li>{@code price} → {@link ProductPrice}</li>
     * </ul>
     *
     * <p>このメソッドでは、永続化層から取得した値の妥当性を検証し、
     * nullまたは不正な形式のデータが存在する場合は{@link DomainException}を送出します。</p>
     *
     * @param input jOOQにより取得された {@link ProductRecord}
     * @return 検証済みの{@link Product}エンティティ(カテゴリと在庫はnullで再構築）
     * @throws DomainException 必須項目がnullまたは不正形式の場合
     */
    @Override
    default Product toDomain(ProductRecord input) {
        if (input == null) {
            throw new DomainException("商品情報が取得できません。");
        }

        var productUuid = input.getProductUuid();
        var name = input.getName();
        var price = input.getPrice();

        if (productUuid == null) {
            throw new DomainException("商品UUIDが不正です。");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("商品名が未設定です。");
        }
        if (price == null) {
            throw new DomainException("商品価格が未設定です。");
        }

        return Product.restore(
            ProductId.fromString(productUuid.toString()),
            ProductName.of(name),
            ProductPrice.of(price),
            (Category) null,
            (Stock) null
        );
    }

    /**
     * エンティティ{@link Product}をjOOQの{@link ProductRecord}に変換する。
     *
     * <p>生成された {@code ProductRecord}はjOOQの{@code DSLContext}に渡して
     * {@code insertInto(...).set(record).execute()}のように使用できます。</p>
     *
     * <p><b>変換ルール：</b></p>
     * <ul>
     *   <li>{@link ProductId} → {@code product_uuid}</li>
     *   <li>{@link ProductName} → {@code name}</li>
     *   <li>{@link ProductPrice} → {@code price}</li>
     * </ul>
     *
     * @param domain ドメインエンティティ{@link Product}
     * @return 永続化用{@link ProductRecord}
     * @throws DomainException 引数がnullの場合
     */
    @Override
    default ProductRecord fromDomain(Product domain) {
        if (domain == null) {
            throw new DomainException("Productエンティティがnullです。");
        }
        var rec = new ProductRecord();
        rec.setProductUuid(UUID.fromString(domain.getProductId().value()));
        rec.setName(domain.getName().value());
        rec.setPrice(domain.getPrice().value());
        return rec;
    }
}

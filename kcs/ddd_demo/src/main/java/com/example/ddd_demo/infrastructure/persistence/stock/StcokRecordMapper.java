package com.example.ddd_demo.infrastructure.persistence.stock;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductStockRecord;

/**
 * jOOQのProductStockRecordとエンティティStockを相互変換するMapper。
 * <p>DDDの腐敗防止層（Anti-Corruption Layer）として機能し、
 * 永続化構造(ProductStockRecord)とドメイン構造(Stock)の間の依存を絶ちます。</p>
 */
@Mapper(componentModel = "spring") // Spring管理Beanとして実装を生成する
public interface StcokRecordMapper extends DomainBiMapper<ProductStockRecord, Stock> {

    /**
     * jOOQのProductStockRecordからドメインエンティティStockを再構築する。
     *
     * @param input ProductStockRecord
     * @return 再構築されたStockエンティティ
     * @throws DomainException 必須項目がnullまたは不正な場合
     */
    @Override
    default Stock toDomain(ProductStockRecord input){
        if (input == null) {
            throw new DomainException("在庫情報が取得できません。");
        }
        var stockUuid = input.getStockUuid();
        var quantity = input.getStock();
        if (stockUuid == null) {
            throw new DomainException("在庫UUIDが不正です。");
        }
        if (quantity == null) {
            throw new DomainException("在庫数が未設定です。");
        }
        return Stock.restore(
            StockId.fromString(stockUuid.toString()),
            StockQuantity.of(quantity)
        );
    } 

    /**
     * ドメインエンティティStockをjOOQのProductStockRecordに変換する。
     * <p>このメソッドはINSERT/UPDATE用に利用可能であり、
     * DB側のIDや外部キー(product_id)は呼び出し元で補完する必要があります。</p>
     * @param domain エンティティStock
     * @return jOOQのProductStockRecord
     */
    @Override
    default ProductStockRecord fromDomain(Stock domain){
        if (domain == null) {
            throw new DomainException("Stockエンティティがnullです。");
        }
        var rec = new ProductStockRecord();
        rec.setStockUuid(UUID.fromString(domain.getStockId().value()));
        rec.setStock(domain.getQuantity().value());
        return rec;
    }
}

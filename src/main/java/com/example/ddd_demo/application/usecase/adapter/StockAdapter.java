package com.example.ddd_demo.application.usecase.adapter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.StockDTO;
import com.example.ddd_demo.domain.adapter.DomainBiAdapter;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * StockエンティティとStockDTOの相互変換Adapter
 * <p>⚠️ MapStruct版 {@code ProductRecordMapper} を導入したため、このクラスは非推奨です。</p>
 * <p>将来的には削除予定です。</p>
 */
@Deprecated(since = "2025-10-26", forRemoval = true)
@Component
public class StockAdapter implements DomainBiAdapter<StockDTO , Stock>{

    /**
     * StockDTOからStockエンティティを再構築する
     * @param input StockDTO
     * @return Stock
     */
    @Override
    public Stock toDomain(StockDTO input) {
        if (input == null){
            throw new InvalidInputException("StockDTOがnullです。");
        }

        String rawId = input.getId();
        String id = (rawId == null) ? null : rawId.trim();
        Integer qty = input.getQuantity();

        if (!StringUtils.hasText(id)) {
            // 新規作成（ID未指定）
            return Stock.createNew(StockQuantity.of(qty));
        } else {
            // 既存再構築
            return Stock.rehydrate(
                StockId.fromString(id),
                StockQuantity.of(qty)
            );
        }
    }

    /**
     * StockエンティティをStockDTOに変換する
     * @param domain Stock
     * @return StockDTO
     */
    @Override
    public StockDTO fromDomain(Stock domain) {
        if (domain == null){
            throw new InvalidInputException("Stockがnullです。");
        }
        return new StockDTO(domain.getStockId().value(),domain.getQuantity().value());
    }
}

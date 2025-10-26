package com.example.ddd_demo.application.usecase.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.StockDTO;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * StockエンティティとStockDTOの相互変換Mapper
 */

@Mapper(componentModel = "spring")
public interface StockMapper extends DomainBiMapper<StockDTO, Stock>{

    /**
     * StockDTOからStockエンティティを再構築する
     * @param dto StockDTO
     * @return Stock
     */
    default Stock toDomain(StockDTO dto) {
        if (dto == null) throw new InvalidInputException("Stockがnullです。");
        if (dto.getQuantity() == null) throw new InvalidInputException("在庫数は必須です。");

        // Id が未設定なら新規作成、設定済みなら再構築
        if (!StringUtils.hasText(dto.getId())) {
            return Stock.createNew(StockQuantity.of(dto.getQuantity()));
        } else {
            return Stock.rehydrate(StockId.fromString(dto.getId()), StockQuantity.of(dto.getQuantity()));
        }
    }

    /**
     * StockエンティティをStockDTOに変換する
     * @param domain Stock
     * @return StockDTO
     */
    default StockDTO fromDomain(Stock domain) {
        if (domain == null) throw new InvalidInputException("Stockがnullです。");
        return new StockDTO(domain.getStockId().value(), domain.getQuantity().value());
    }
}

package com.example.ddd_demo.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.dto.StockDTO;
import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * {@link Stock} エンティティと {@link StockDTO} の
 * 相互変換を行うMapper。
 *
 * <p>在庫数量やIDのNull検証を行い、Entity再構築時の妥当性を担保する。</p>
 */
@Mapper(componentModel = "spring")
public interface StockMapper extends DomainBiMapper<StockDTO, Stock>{

    /**
     * StockDTO から Stock エンティティを再構築する。
     * 
     * @param dto StockDTO
     * @return Stock エンティティ
     * @throws InvalidInputException DTOの必須値が欠落している場合
     */
    default Stock toDomain(StockDTO dto) {
        if (dto == null) throw new InvalidInputException("Stockがnullです。");
        if (dto.getQuantity() == null) throw new InvalidInputException("在庫数は必須です。");

        // Id が未設定なら新規作成、設定済みなら再構築
        if (!StringUtils.hasText(dto.getId())) {
            return Stock.createNew(StockQuantity.of(dto.getQuantity()));
        } else {
            return Stock.restore(StockId.fromString(dto.getId()), StockQuantity.of(dto.getQuantity()));
        }
    }

    /**
     * Stock エンティティを StockDTO に変換する。
     *
     * @param domain Stock エンティティ
     * @return StockDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    default StockDTO fromDomain(Stock domain) {
        if (domain == null) throw new InvalidInputException("Stockがnullです。");
        return new StockDTO(domain.getStockId().value(), domain.getQuantity().value());
    }
}

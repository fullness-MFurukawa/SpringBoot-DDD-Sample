package com.example.ddd_demo.infrastructure.persistence.stock;
import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductStockRecord;

/**
 * StockRecordMapper（StcokRecordMapper）の単体テストドライバ
 *
 * <p>Mapperが正しく双方向変換できるかを検証する。</p>
 */
@ContextConfiguration(classes = { StcokRecordMapperImpl.class }) // MapStruct生成クラスを直接読み込む
@SpringBootTest
public class StockRecordMapperTest {
    /**
     * テストターゲット
     */
    @Autowired
    private StcokRecordMapper mapper;

    @Nested
    class 正常系 {

        @Test
        @DisplayName("ProductStockRecord → Stockへの変換が正しく行われる")
        void toDomain_success() {
            var record = new ProductStockRecord();
            UUID stockUuid = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
            record.setStockUuid(stockUuid);
            record.setStock(100);

            Stock stock = mapper.toDomain(record);

            assertThat(stock).isNotNull();
            assertThat(stock.getStockId().value()).isEqualTo(stockUuid.toString());
            assertThat(stock.getQuantity().value()).isEqualTo(100);
        }

        @Test
        @DisplayName("Stock → ProductStockRecordへの変換が正しく行われる")
        void fromDomain_success() {
            UUID stockUuid = UUID.fromString("cccccccc-1111-2222-3333-dddddddddddd");
            var stock = Stock.restore(
                StockId.fromString(stockUuid.toString()),
                StockQuantity.of(50)
            );

            ProductStockRecord record = mapper.fromDomain(stock);

            assertThat(record).isNotNull();
            assertThat(record.getStockUuid()).isEqualTo(stockUuid);
            assertThat(record.getStock()).isEqualTo(50);
        }
    }

    @Nested
    class 異常系 {

        @Test
        @DisplayName("ProductStockRecordがnullの場合はDomainExceptionをスローする")
        void toDomain_nullRecord() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("在庫情報");
        }

        @Test
        @DisplayName("Stockがnullの場合はDomainExceptionをスローする")
        void fromDomain_nullDomain() {
            assertThatThrownBy(() -> mapper.fromDomain(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Stockエンティティ");
        }

        @Test
        @DisplayName("StockUUIDがnullの場合はDomainExceptionをスローする")
        void toDomain_nullUuid() {
            var record = new ProductStockRecord();
            record.setStockUuid(null);
            record.setStock(100);

            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("在庫UUID");
        }

        @Test
        @DisplayName("在庫数がnullの場合はDomainExceptionをスローする")
        void toDomain_nullStockValue() {
            var record = new ProductStockRecord();
            record.setStockUuid(UUID.randomUUID());
            record.setStock(null);

            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("在庫数");
        }
    }
}

package com.example.ddd_demo.application.usecase.adapter;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.StockDTO;
import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * StockエンティティとStockDTOの相互変換Adapterのテストドライバ
 */
@SpringBootTest
public class StockAdapterTest {
    @Autowired
    private StockAdapter adapter;
    
    @Nested
    class 正常系 {

        @Test
        @DisplayName("toDomain(): ID未指定(null/空白)なら新規作成として扱う")
        void toDomain_createNew_when_id_blank() {
            var dto = new StockDTO(null, 10);

            var result = adapter.toDomain(dto);

            assertThat(result.getQuantity().value()).isEqualTo(10);
            assertThat(result.getStockId().value())
                .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("toDomain(): ID指定ありなら再構築として扱う")
        void toDomain_rehydrate() {
            var id = "8b5f0f71-fb54-42e1-8d0e-9789ef2ddba8";
            var dto = new StockDTO(id, 5);

            var result = adapter.toDomain(dto);

            assertThat(result.getStockId().value()).isEqualTo(id);
            assertThat(result.getQuantity().value()).isEqualTo(5);
        }

        @Test
        @DisplayName("fromDomain(): 正しくDTOへ変換できる")
        void fromDomain_success() {
            var id = StockId.fromString("8b5f0f71-fb54-42e1-8d0e-9789ef2ddba8");
            var qty = StockQuantity.of(12);
            var domain = Stock.rehydrate(id, qty);

            var dto = adapter.fromDomain(domain);

            assertThat(dto.getId()).isEqualTo(id.value());
            assertThat(dto.getQuantity()).isEqualTo(12);
        }
    }

    @Nested
    class 異常系 {
        @Test
        @DisplayName("toDomain(): 入力がnullならInvalidInputExceptionをスローする")
        void toDomain_null_throws() {
            assertThatThrownBy(() -> adapter.toDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("StockDTOがnullです。");
        }

        @Test
        @DisplayName("fromDomain(): 入力がnullならInvalidInputExceptionをスローする")
        void fromDomain_null_throws() {
            assertThatThrownBy(() -> adapter.fromDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Stockがnullです。");
        }

        @Test
        @DisplayName("toDomain(): 数量が負数ならDomainExceptionをスローする")
        void toDomain_negative_quantity_throws() {
            var dto = new StockDTO(null, -5);

            assertThatThrownBy(() -> adapter.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("在庫数");
        }

        @Test
        @DisplayName("toDomain(): 不正なID形式ならDomainExceptionをスローする")
        void toDomain_invalid_id_throws() {
            var dto = new StockDTO("invalid-uuid", 10);

            assertThatThrownBy(() -> adapter.toDomain(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UUID");
        }
    }
}

package com.example.ddd_demo.infrastructure.persistence.product;

import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * jOOQのRecordからProductエンティティを再構築する
 * MapStruct版のProductRecordMapperの単体テストドライバ
 */
@SpringBootTest
public class ProductRecordMapperTest {
    @Autowired
    private DSLContext dsl;

    /** テスト対象(MapStructの生成bean) */
    @Autowired
    private ProductRecordMapper mapper;

    /** テスト用のRecord6を生成するヘルパー */
    private Record6<UUID, String, Integer, UUID, Integer, UUID> createRecord(
            UUID productUuid,
            String name,
            Integer price,
            UUID stockUuid,
            Integer qty,
            UUID categoryUuid) {

        return dsl.select(
                    DSL.val(productUuid,  SQLDataType.UUID),
                    DSL.val(name,         SQLDataType.VARCHAR.length(30)),
                    DSL.val(price,        SQLDataType.INTEGER),
                    DSL.val(stockUuid,    SQLDataType.UUID),
                    DSL.val(qty,          SQLDataType.INTEGER),
                    DSL.val(categoryUuid, SQLDataType.UUID)
               )
               .fetchOne(); // Record6<UUID,String,Integer,UUID,Integer,UUID>
    }

    @Nested
    class 正常系 {
        @Test
        @DisplayName("Record6からProductエンティティを正しく再構築できる")
        void toDomain_success() {
            UUID productUuid  = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
            UUID stockUuid    = UUID.fromString("22222222-2222-2222-2222-222222222222");
            UUID categoryUuid = UUID.fromString("22222222-2222-2222-2222-222222222222");

            Record6<UUID,String,Integer,UUID,Integer,UUID> record =
                createRecord(productUuid, "えんぴつ", 120, stockUuid, 10, categoryUuid);

            Product product = mapper.toDomain(record);

            assertThat(product.getProductId().value()).isEqualTo(productUuid.toString());
            assertThat(product.getName().value()).isEqualTo("えんぴつ");
            assertThat(product.getPrice().value()).isEqualTo(120);
            assertThat(product.currentStock().value()).isEqualTo(10);
            assertThat(product.currentStock()).isEqualTo(StockQuantity.of(10));
            assertThat(product.getCategoryId().value()).isEqualTo(categoryUuid.toString());
        }
    }

    @Nested
    class 異常系 {
        @Test
        @DisplayName("商品名が空白のみの場合はDomainExceptionをスローする")
        void invalid_name_blank() {
            Record6<UUID,String,Integer,UUID,Integer,UUID> record =
                createRecord(UUID.randomUUID(), "   ", 120, UUID.randomUUID(), 10, UUID.randomUUID());

            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品名");
        }

        @Test
        @DisplayName("商品単価が下限未満(50未満)の場合はDomainExceptionをスローする")
        void invalid_price_tooLow() {
            Record6<UUID,String,Integer,UUID,Integer,UUID> record =
                createRecord(UUID.randomUUID(), "消しゴム", 49, UUID.randomUUID(), 10, UUID.randomUUID());

            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品単価は");
        }

        @Test
        @DisplayName("商品在庫数が上限超過(>100)の場合はDomainExceptionをスローする")
        void invalid_quantity_tooHigh() {
            Record6<UUID,String,Integer,UUID,Integer,UUID> record =
                createRecord(UUID.randomUUID(), "定規", 200, UUID.randomUUID(), 101, UUID.randomUUID());

            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("在庫数は");
        }

        @Test
        @DisplayName("変換対象がnullの場合はDomainExceptionをスローする")
        void null_record() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOfAny(DomainException.class);
        }
    }
}

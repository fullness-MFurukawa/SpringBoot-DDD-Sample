package com.example.ddd_demo.infrastructure.persistence.product;

import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductRecord;

/**
 * jOOQのRecordからProductエンティティを再構築する
 * MapStruct版のProductRecordMapperの単体テストドライバ
 */
@ContextConfiguration(classes = { ProductRecordMapperImpl.class }) // MapStruct生成クラスを直接読み込む
@SpringBootTest
public class ProductRecordMapperTest {
    
    /** テスト対象(MapStructの生成bean) */
    @Autowired
    private ProductRecordMapper mapper;

    /** テスト用の ProductRecord を生成 */
    private ProductRecord pr(UUID productUuid, String name, Integer price) {
        var rec = new ProductRecord();
        rec.setProductUuid(productUuid);
        rec.setName(name);
        rec.setPrice(price);
        return rec;
    }

    @Nested
    class 正常系 {
        @Test
        @DisplayName("toDomain(): ProductRecordからid/name/priceでProductを再構築できる")
        void toDomain_success() {
             UUID productUuid = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
            ProductRecord record = pr(productUuid, "えんぴつ", 120);

            Product product = mapper.toDomain(record);

            assertThat(product.getProductId().value()).isEqualTo(productUuid.toString());
            assertThat(product.getName().value()).isEqualTo("えんぴつ");
            assertThat(product.getPrice().value()).isEqualTo(120);
        }

        @Test
        @DisplayName("fromDomain(): ProductからProductRecordへ変換できる(外部キーは未設定)")
        void fromDomain_success() {
            // Category / Stock を最小構成で用意（Product生成用）
            var category = Category.restore(
                CategoryId.fromString("22222222-2222-2222-2222-222222222222"),
                CategoryName.of("文房具")
            );
            var stock = Stock.restore(
                StockId.fromString("33333333-3333-3333-3333-333333333333"),
                StockQuantity.of(10)
            );
            var product = Product.restore(
                ProductId.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb"),
                ProductName.of("えんぴつ"),
                ProductPrice.of(120),
                category,
                stock
            );

            ProductRecord rec = mapper.fromDomain(product);
            assertThat(rec.getProductUuid())
                .isEqualTo(UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb"));
            assertThat(rec.getName()).isEqualTo("えんぴつ");
            assertThat(rec.getPrice()).isEqualTo(120);
            // 重要：category_id(INT FK) はここでは未設定（nullのまま）
            assertThat(rec.getCategoryId()).isNull();
        }
    }

    @Nested
    class 異常系 {
        @Test
        @DisplayName("toDomain(): product_uuidがnullの場合はDomainExceptionをスローする")
        void invalid_name_blank() {
            ProductRecord record = pr(null, "名前", 120);
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品UUID");
        }

        @Test
        @DisplayName("toDomain(): nameが空白のみの場合はDomainExceptionをスローする")
        void invalid_price_tooLow() {
            ProductRecord record = pr(UUID.randomUUID(), "   ", 120);
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品名");
        }

        @Test
        @DisplayName("toDomain(): priceがnullの場合はDomainExceptionをスローする")
        void invalid_quantity_tooHigh() {
             ProductRecord record = pr(UUID.randomUUID(), "消しゴム", null);
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("商品価格");
        }

        @Test
        @DisplayName("toDomain(): 単価が50未満の場合はDomainExceptionをスローする")
        void toDomain_price_tooLow() {
            ProductRecord record = pr(UUID.randomUUID(), "定規", 49);
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("単価");
        }

        @Test
        @DisplayName("toDomain(): 単価が10000を超える場合はDomainExceptionをスローする")
        void toDomain_price_tooHigh() {
            ProductRecord record = pr(UUID.randomUUID(), "高級ノート", 10001);
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("単価");
        }

        @Test
        @DisplayName("toDomain(): 引数がnullの場合はDomainExceptionをスローする")
        void null_record() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOfAny(DomainException.class);
        }

        @Test
        @DisplayName("fromDomain(): 引数がnullの場合はDomainExceptionをスローする")
        void fromDomain_null_domain() {
            assertThatThrownBy(() -> mapper.fromDomain(null))
                .isInstanceOf(DomainException.class);
        }
    }
}

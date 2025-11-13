package com.example.ddd_demo.infrastructure.persistence.product;
import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.stock.StockQuantity;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductCategoryRecord;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductRecord;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductStockRecord;

/**
 * {@link ProductAssembler} の単体テストクラス
 *
 * <p>このクラスでは、ProductAssembler の以下の機能を総合的に検証する：</p>
 * <ul>
 *   <li><b>assemble()</b> ― Recordからドメイン集約(Product)を合成できること</li>
 *   <li><b>toProductRecord()</b> ― 集約をProductRecordに変換できること</li>
 *   <li><b>toStockRecord()</b> ― 集約をProductStockRecordに変換できること</li>
 *   <li><b>extractCategoryUuid()</b> ― 集約からCategoryのUUIDを取得できること</li>
 * </ul>
 *
 * <p>MapStructによって自動生成された Mapper Bean
 * （ProductRecordMapper / CategoryRecordMapper / StockRecordMapper）を
 * Spring Boot コンテナ上で利用するため、{@code @SpringBootTest} により
 * Spring コンテキストを起動している。</p>
 */
@SpringBootTest
public class ProductAssemblerTest {
    /**
     * テストターゲット
     */
    @Autowired
    private ProductAssembler assembler;

     /**
     * ProductRecordを生成するヘルパー
     */
    private ProductRecord pr(UUID productUuid, String name, int price) {
        var pr = new ProductRecord();
        pr.setProductUuid(productUuid);
        pr.setName(name);
        pr.setPrice(price);
        return pr;
    }

    /**
     * ProductCategoryRecordを生成するヘルパー。
     * CategoryのUUIDと名称を設定する。
     */
    private ProductCategoryRecord cr(UUID categoryUuid, String name) {
        var cr = new ProductCategoryRecord();
        cr.setCategoryUuid(categoryUuid);
        cr.setName(name);
        return cr;
    }

    /**
     * ProductStockRecordを生成するヘルパー。
     * StockのUUIDと数量を設定する。
     */
    private ProductStockRecord sr(UUID stockUuid, int qty) {
        var sr = new ProductStockRecord();
        sr.setStockUuid(stockUuid);
        sr.setStock(qty);
        return sr;
    }

    @Nested
    class 合成テスト {
         /**
         * 【正常系】
         * <p>3種類の jOOQ Record（Product / Category / Stock）から
         * 完全な Product 集約を正しく合成できることを確認する。</p>
         *
         * <p>このテストでは、ProductAssembler#assemble() が
         * Mapperを正しく利用し、Category・Stockを内部に保持した
         * 完全なProductエンティティを再構築できることを検証する。</p>
         */
        @Test
        @DisplayName("assemble(): 3つのRecordから完全なProduct集約を合成できる")
        void assemble_success() {
            // Arrange（入力データを準備）
            UUID pUuid = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
            UUID cUuid = UUID.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
            UUID sUuid = UUID.fromString("22222222-2222-2222-2222-222222222222");

            var pr = pr(pUuid, "えんぴつ", 120);
            var cr = cr(cUuid, "文房具");
            var sr = sr(sUuid, 10);

            // Act（テスト対象メソッド呼び出し）
            Product product = assembler.assemble(pr, cr, sr);

            // Assert（結果検証）
            // --- Product基本情報 ---
            assertThat(product.getProductId().value()).isEqualTo(pUuid.toString());
            assertThat(product.getName().value()).isEqualTo("えんぴつ");
            assertThat(product.getPrice().value()).isEqualTo(120);

            // --- Category ---
            assertThat(product.getCategory().getCategoryId().value()).isEqualTo(cUuid.toString());
            assertThat(product.getCategory().getName().value()).isEqualTo("文房具");

            // --- Stock ---
            assertThat(product.currentStock().value()).isEqualTo(10);
            assertThat(product.getStock().getStockId().value()).isEqualTo(sUuid.toString());
        }

        /**
         * 【異常系】
         * <p>assemble() の引数が null の場合に、適切な DomainException が送出されることを検証する。</p>
         */
        @Test
        @DisplayName("assemble(): 引数がnullの場合はDomainExceptionをスローする")
        void assemble_null_throws() {
            var validPr = pr(UUID.randomUUID(), "A", 100);
            var validCr = cr(UUID.randomUUID(), "カテゴリ");
            var validSr = sr(UUID.randomUUID(), 5);

            assertThatThrownBy(() -> assembler.assemble(null, validCr, validSr))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("ProductRecordがnull");

            assertThatThrownBy(() -> assembler.assemble(validPr, null, validSr))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("ProductCategoryRecordがnull");

            assertThatThrownBy(() -> assembler.assemble(validPr, validCr, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("ProductStockRecord がnull");
        }
    }

    @Nested
    class 分解テスト {
        /**
         * 【正常系】
         * <p>Product集約からProductRecordを生成できることを検証する。</p>
         *
         * <p>Repository層の方針として、外部キーであるcategory_idはここでは
         * 設定しないため、nullであることも併せて確認する。</p>
         */
        @Test
        @DisplayName("toProductRecord(): 集約からProductRecordを生成できる（category_idは未設定）")
        void toProductRecord_success() {
            // Arrange
            var cat = Category.restore(
                CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4"),
                CategoryName.of("文房具")
            );
            var product = Product.createNew(
                ProductName.of("シャープペンシル"),
                ProductPrice.of(150),
                cat,
                StockQuantity.of(30)
            );

            // Act
            ProductRecord rec = assembler.toProductRecord(product);

            // Assert
            assertThat(rec.getProductUuid()).isEqualTo(UUID.fromString(product.getProductId().value()));
            assertThat(rec.getName()).isEqualTo("シャープペンシル");
            assertThat(rec.getPrice()).isEqualTo(150);
            assertThat(rec.getCategoryId()).isNull(); // 外部キーはRepositoryで補完
        }

         /**
         * 【正常系】
         * <p>Product集約からProductStockRecordを生成できることを検証する。</p>
         *
         * <p>Repository層で product_id を補完するため、
         * 本メソッドでは未設定（null）であることを確認する。</p>
         */
        @Test
        @DisplayName("toStockRecord(): 集約からProductStockRecordを生成できる（product_idは未設定）")
        void toStockRecord_success() {
            var cat = Category.restore(
                CategoryId.fromString("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4"),
                CategoryName.of("文房具")
            );
            var product = Product.createNew(
                ProductName.of("シャープペンシル"),
                ProductPrice.of(150),
                cat,
                StockQuantity.of(30)
            );

            var sr = assembler.toStockRecord(product);

            assertThat(sr.getStockUuid()).isEqualTo(UUID.fromString(product.getStock().getStockId().value()));
            assertThat(sr.getStock()).isEqualTo(30);
            assertThat(sr.getProductId()).isNull(); // 外部キーはRepositoryで補完
        }

        /**
         * 【異常系】
         * <p>nullを渡した場合、DomainExceptionが送出されることを検証する。</p>
         */
        @Test
        @DisplayName("toProductRecord()/toStockRecord(): 引数がnullの場合はDomainException")
        void toRecords_null_throws() {
            assertThatThrownBy(() -> assembler.toProductRecord(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Productがnull");

            assertThatThrownBy(() -> assembler.toStockRecord(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Productがnull");
        }
    }

    @Nested
    class ユーティリティテスト {
         /**
         * 【正常系】
         * <p>Product集約からCategoryのUUIDを正しく抽出できることを検証する。</p>
         */
        @Test
        @DisplayName("extractCategoryUuid(): 集約からカテゴリUUID文字列を取得できる")
        void extractCategoryUuid_success() {
            var categoryId = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";
            var cat = Category.restore(
                CategoryId.fromString(categoryId),
                CategoryName.of("文房具")
            );
            var product = Product.createNew(
                ProductName.of("定規"),
                ProductPrice.of(200),
                cat,
                StockQuantity.of(5)
            );

            var uuidStr = assembler.extractCategoryUuid(product);

            assertThat(uuidStr).isEqualTo(categoryId);
        }

        /**
         * 【異常系】
         * <p>引数がnull、またはCategory未設定のProductを渡した場合、
         * DomainExceptionが送出されることを検証する。</p>
         */
        @Test
        @DisplayName("extractCategoryUuid(): ProductがnullまたはCategory未設定の場合はDomainException")
        void extractCategoryUuid_throws() {
            assertThatThrownBy(() -> assembler.extractCategoryUuid(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Productがnull");

            // Category未設定の場合の確認（Productの骨格がnullカテゴリーで生成されるケース）
            assertThatThrownBy(() -> {
                var skeleton = Product.restore(
                    Product.createNew(
                        ProductName.of("えんぴつ"),
                        ProductPrice.of(100),
                        Category.restore(CategoryId.fromString("00000000-0000-0000-0000-000000000000"),
                            CategoryName.of("文房具")),
                        StockQuantity.of(10)
                    ).getProductId(),
                    ProductName.of("えんぴつ"),
                    ProductPrice.of(100),
                    null,
                    null
                );
                assembler.extractCategoryUuid(skeleton);
            }).isInstanceOf(DomainException.class);
        }
    }
}

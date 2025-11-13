package com.example.ddd_demo.application.mapper;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.dto.StockDTO;
import com.example.ddd_demo.application.exception.InvalidInputException;
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

/**
 * ProductDtoAssembler の単体テストドライバ
 */
@SpringBootTest
public class ProductDtoAssemblerTest {
    /** 
     * テストターゲット 
     */
    @Autowired
    private ProductDTOAssembler assembler;

    // ------------------------------------------------------------
    // ヘルパー: DTO 生成
    // ------------------------------------------------------------
    private ProductDTO pDto(String pid, String name, Integer price,
                            String cid, String cname,
                            String sid, Integer qty) {
        var product = new ProductDTO();
        product.setId(pid);
        product.setName(name);
        product.setPrice(price);
        product.setCategory(new CategoryDTO(cid, cname));
        product.setStock(new StockDTO(sid, qty));
        return product;
    }

    // ------------------------------------------------------------
    // ヘルパー: Domain 生成
    // ------------------------------------------------------------
    private Category cat(String id, String name) {
        return Category.restore(CategoryId.fromString(id), CategoryName.of(name));
    }

    private Stock stock(String id, Integer qty) {
        if (id == null || id.isBlank()) {
            return Stock.createNew(StockQuantity.of(qty));
        }
        return Stock.restore(StockId.fromString(id), StockQuantity.of(qty));
    }

    private Product product(String pid, String name, Integer price,
                            Category category, Stock stock) {
        return Product.restore(
            ProductId.fromString(pid),
            ProductName.of(name),
            ProductPrice.of(price),
            category,
            stock
        );
    }

    @Nested
    class 正常系 {
        @Test
        @DisplayName("assembleDomain(): 完全なDTO(商品/カテゴリ/在庫)から集約Productを合成できる")
        void assembleDomain_success() {
            var pid = "83fbc81d-2498-4da6-b8c2-54878d3b67ff";
            var cid = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";
            var sid = "11111111-2222-3333-4444-555555555555";

            var dto = pDto(pid, "蛍光ペン(赤)", 130, cid, "文房具", sid, 50);

            Product result = assembler.assembleDomain(dto);

            assertThat(result.getProductId().value()).isEqualTo(pid);
            assertThat(result.getName().value()).isEqualTo("蛍光ペン(赤)");
            assertThat(result.getPrice().value()).isEqualTo(130);

            assertThat(result.getCategory()).isNotNull();
            assertThat(result.getCategory().getCategoryId().value()).isEqualTo(cid);
            assertThat(result.getCategory().getName().value()).isEqualTo("文房具");

            assertThat(result.getStock()).isNotNull();
            assertThat(result.getStock().getStockId().value()).isEqualTo(sid);
            assertThat(result.getStock().getQuantity().value()).isEqualTo(50);
        }

        @Test
        @DisplayName("assembleDto(): 合成済みProductから入れ子のProductDTOへ分解できる")
        void assembleDto_success() {
            var pid = "e4850253-f363-4e79-8110-7335e4af45be";
            var cid = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";
            var sid = "99999999-aaaa-bbbb-cccc-dddddddddddd";

            var domain = product(
                pid, "鉛筆(黒)", 100,
                cat(cid, "文房具"),
                stock(sid, 100)
            );

            ProductDTO dto = assembler.assembleDto(domain);

            assertThat(dto.getId()).isEqualTo(pid);
            assertThat(dto.getName()).isEqualTo("鉛筆(黒)");
            assertThat(dto.getPrice()).isEqualTo(100);

            assertThat(dto.getCategory()).isNotNull();
            assertThat(dto.getCategory().getId()).isEqualTo(cid);
            assertThat(dto.getCategory().getName()).isEqualTo("文房具");

            assertThat(dto.getStock()).isNotNull();
            assertThat(dto.getStock().getId()).isEqualTo(sid);
            assertThat(dto.getStock().getQuantity()).isEqualTo(100);
        }
    }
    
    @Nested
    class 異常系 {
        @Test
        @DisplayName("assembleDomain(): ProductDTO=nullならInvalidInputExceptionをスローする")
        void assembleDomain_null_product_throws() {
            assertThatThrownBy(() -> assembler.assembleDomain(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("ProductDTOがnullです。");
        }

        @Test
        @DisplayName("assembleDomain(): CategoryDTO=nullならInvalidInputExceptionをスローする")
        void assembleDomain_null_category_throws() {
            var dto = new ProductDTO();
            dto.setId("83fbc81d-2498-4da6-b8c2-54878d3b67ff");
            dto.setName("マーカー(青)");
            dto.setPrice(180);
            dto.setCategory(null);               // ← null
            dto.setStock(new StockDTO(null, 20));

            assertThatThrownBy(() -> assembler.assembleDomain(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("CategoryDTOがnullです。");
        }

        @Test
        @DisplayName("assembleDomain(): StockDTO=nullならInvalidInputExceptionをスローする")
        void assembleDomain_null_stock_throws() {
            var dto = new ProductDTO();
            dto.setId("83fbc81d-2498-4da6-b8c2-54878d3b67ff");
            dto.setName("マーカー(黒)");
            dto.setPrice(180);
            dto.setCategory(new CategoryDTO("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具"));
            dto.setStock(null);                  // ← null

            assertThatThrownBy(() -> assembler.assembleDomain(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("StockDTOがnullです。");
        }

        @Test
        @DisplayName("assembleDto(): Product=nullならInvalidInputExceptionをスローする")
        void assembleDto_null_domain_throws() {
            assertThatThrownBy(() -> assembler.assembleDto(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Productがnullです。");
        }
    }
}

package com.example.ddd_demo.application.usecase.adapter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.domain.adapter.DomainBiAdapter;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;

import lombok.RequiredArgsConstructor;

/**
 * ProductエンティティとProductDTOの相互変換Adapter
 * <p>⚠️ MapStruct版 {@code ProductRecordMapper} を導入したため、このクラスは非推奨です。</p>
 * <p>将来的には削除予定です。</p>
 */
@Deprecated(since = "2025-10-26", forRemoval = true)
@Component
@RequiredArgsConstructor
public class ProductAdapter implements DomainBiAdapter<ProductDTO, Product>{

    /**
     * CategoryエンティティとCategoryDTOの相互変換Adapter
     */
    private final CategoryAdapter categoryAdapter;
    /**
     * StockエンティティとStockDTOの相互変換Adapter
     */
    private final StockAdapter stockAdapter;

    /**
     * ProductDTOからProductエンティティを再構築する
     * @param input ProductDTO
     * @return Product
     */
    @Override
    public Product toDomain(ProductDTO input) {
        if (input == null) throw new InvalidInputException("ProductDTOがnullです。");
        if (input.getCategory() == null) throw new InvalidInputException("商品カテゴリは必須です。");
        if (input.getStock() == null) throw new InvalidInputException("商品在庫は必須です。");
    
        // Categoryを再構築する
        var category = categoryAdapter.toDomain(input.getCategory());
        // Stockを再構築する
        var stock = stockAdapter.toDomain(input.getStock());
        // 商品Idが未設定の場合は新規
        if (!StringUtils.hasText(input.getId())) {
            // 新規作成
            return Product.createNew(
                ProductName.of(input.getName()),
                ProductPrice.of(input.getPrice()),
                category.getCategoryId(),
                stock.getQuantity()
            );
        } else {
            // 再構築
            return Product.rehydrate(
                ProductId.fromString(input.getId()),
                ProductName.of(input.getName()),
                ProductPrice.of(input.getPrice()),
                category.getCategoryId(),
                stock
            );
        }
    }

    /**
     * ProductエンティティをProductDTOに変換する
     * @param domain Product
     * @return ProductDTO
     */
    @Override
    public ProductDTO fromDomain(Product domain) {
        if (domain == null) throw new InvalidInputException("Productがnullです。");
        var categoryDto = new CategoryDTO(domain.getCategoryId().value(),"");
        var stockDto = stockAdapter.fromDomain(domain.getStock());
        return new ProductDTO(
            domain.getProductId().value(),
            domain.getName().value(),
            domain.getPrice().value(),
            categoryDto,
            stockDto
        );
    }
}

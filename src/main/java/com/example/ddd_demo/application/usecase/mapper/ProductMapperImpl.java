package com.example.ddd_demo.application.usecase.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.application.usecase.dto.CategoryDTO;
import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.application.usecase.dto.StockDTO;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;

/**
 * ProductエンティティとProductDTOの相互変換Mapperインターフェイスの実装
 */
@Component
public class ProductMapperImpl implements ProductMapper{
    /**
     * CategoryエンティティとCategoryDTOの相互変換Mapper
     */
    private final CategoryMapper categoryMapper;
    /**
     * StockエンティティとStockDTOの相互変換Mapper
     */
    private final StockMapper stockMapper;

    /**
     * コンストラクタ
     * @param categoryMapper CategoryエンティティとCategoryDTOの相互変換Mapper
     * @param stockMapper StockエンティティとStockDTOの相互変換Mapper
     */
    public ProductMapperImpl(CategoryMapper categoryMapper, StockMapper stockMapper) {
        this.categoryMapper = categoryMapper;
        this.stockMapper = stockMapper;
    }

    /**
     * ProductDTOからProductエンティティを再構築する
     * @param dto  ProductDTO
     * @return Product
     */
    @Override
    public Product toDomain(ProductDTO dto) {
        if (dto == null) 
            throw new InvalidInputException("ProductDTOがnullです。");
        if (dto.getCategory() == null) 
            throw new InvalidInputException("商品カテゴリは必須です。");
        if (dto.getStock() == null) 
            throw new InvalidInputException("商品在庫は必須です。");
    
        // Categoryを再構築する
        var category = categoryMapper.toDomain(dto.getCategory());
        // Stockを再構築する
        var stock = stockMapper.toDomain(dto.getStock());
        // 商品Idが未設定の場合は新規
        if (!StringUtils.hasText(dto.getId())) {
            // 新規作成
            return Product.createNew(
                ProductName.of(dto.getName()),
                ProductPrice.of(dto.getPrice()),
                category.getCategoryId(),
                stock.getQuantity()
            );
        } else {
            // 再構築
            return Product.rehydrate(
                ProductId.fromString(dto.getId()),
                ProductName.of(dto.getName()),
                ProductPrice.of(dto.getPrice()),
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
        if (domain == null) 
            throw new InvalidInputException("Productがnullです。");
        var categoryDto = new CategoryDTO(domain.getCategoryId().value(),"");
        var stockDto = stockMapper.fromDomain(domain.getStock());
        return new ProductDTO(
            domain.getProductId().value(),
            domain.getName().value(),
            domain.getPrice().value(),
            categoryDto,
            stockDto
        );
    }
}

package com.example.ddd_demo.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.exception.InvalidInputException;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;

/**
 * {@link Product} エンティティと {@link ProductDTO} の相互変換を行うMapper。
 *
 * <p>商品情報のスケルトン（id, name, price のみ）を再構築・変換する。</p>
 *
 * <p>カテゴリ・在庫情報は本Mapperでは扱わず、
 * {@link com.example.ddd_demo.application.mapper.ProductDTOAssembler} が担当する。</p>
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends DomainBiMapper<ProductDTO, Product>{

    /**
     * ProductDTO から Product エンティティをスケルトンとして再構築する。
     *
     * <p>IDが未設定の場合は新規生成し、
     * 値オブジェクト（ProductName, ProductPrice）の検証を実施。</p>
     *
     * @param dto ProductDTO
     * @return Product（カテゴリ・在庫は含まないスケルトン）
     * @throws InvalidInputException 必須項目が欠落している場合
     */
    @Override
    default Product toDomain(ProductDTO dto) {
        if (dto == null) 
            throw new InvalidInputException("ProductDTOがnullです。");
        if (!StringUtils.hasText(dto.getName())) 
            throw new InvalidInputException("商品名は必須です。");
        if (dto.getPrice() == null)
            throw new InvalidInputException("商品単価は必須です。");
        return Product.restoreSkeleton(
            dto.getId() != null ? ProductId.fromString(dto.getId()) : ProductId.createNew(),
            ProductName.of(dto.getName()),
            ProductPrice.of(dto.getPrice())
        );
    }

    /**
     * Product エンティティを ProductDTO に変換する。
     *
     * <p>カテゴリ・在庫情報は含まず、スケルトンDTOを生成。</p>
     *
     * @param domain Product
     * @return ProductDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    @Override
    default ProductDTO fromDomain(Product domain) {
        if (domain == null) 
            throw new InvalidInputException("Productがnullです。");
        if (domain.getName() == null) 
            throw new InvalidInputException("商品名は必須です。");
        if (domain.getPrice() == null) 
            throw new InvalidInputException("商品単価は必須です。");
        return new ProductDTO(
            domain.getProductId().value(),
            domain.getName().value(),
            domain.getPrice().value(),
            null,null  
        );
    }
}
package com.example.ddd_demo.application.usecase.mapper;

import com.example.ddd_demo.application.usecase.dto.ProductDTO;
import com.example.ddd_demo.domain.mapper.DomainBiMapper;
import com.example.ddd_demo.domain.models.product.Product;

/**
 * ProductエンティティとProductDTOの相互変換Mapper
 */
public interface ProductMapper extends DomainBiMapper<ProductDTO, Product>{
}
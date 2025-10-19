package com.example.ddd_demo.infrastructure.persistence.product;

import java.util.UUID;
import org.jooq.Record6;
import org.springframework.stereotype.Component;

import com.example.ddd_demo.domain.adapter.ToDomainAdapter;
import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductPrice;
import com.example.ddd_demo.domain.models.stock.Stock;
import com.example.ddd_demo.domain.models.stock.StockId;
import com.example.ddd_demo.domain.models.stock.StockQuantity;

/**
 * jOOQのRecordからProductエンティティを再構築するAdapterクラス
 */
@Component
public class ProductRecordAdapter 
implements ToDomainAdapter<Record6<UUID, String, Integer, UUID, Integer, UUID>, Product>{

    /**
     * jOOQのRecordからProductエンティティを再構築する
     * @param input JooQのRecored
     * @return Productエンティティ
     */
    @Override
    public Product toDomain(Record6<UUID, String, Integer, UUID, Integer, UUID> input) {
        if (input == null) throw new DomainException("商品情報が取得できません。");
        // 商品Idを生成する
        var productId = ProductId.fromString(input.value1().toString());
        // 商品名を生成する
        var name = ProductName.of(input.value2());
        // 商品単価を生成する
        var price = ProductPrice.of(input.value3());
        // 商品在庫Idを生成する
        var stockId = StockId.fromString(input.value4().toString());
        // 商品在庫を生成する
        var quantity = StockQuantity.of(input.value5());
        // 商品在庫エンティティを再構築する
        var stock = Stock.rehydrate(stockId, quantity);
        // 商品カテゴリId
        var categoryId  = CategoryId.fromString(input.value6().toString()); 
        // 商品エンティティを再構築して返す
        return Product.rehydrate(productId, name, price, categoryId, stock);
    }
}

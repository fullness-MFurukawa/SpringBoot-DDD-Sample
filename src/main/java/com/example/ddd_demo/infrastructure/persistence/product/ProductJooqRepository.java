package com.example.ddd_demo.infrastructure.persistence.product;

import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.product.Product;
import com.example.ddd_demo.domain.models.product.ProductId;
import com.example.ddd_demo.domain.models.product.ProductName;
import com.example.ddd_demo.domain.models.product.ProductRepository;
import com.example.ddd_demo.infrastructure.exception.InternalException;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductCategoryTable;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductStockTable;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductTable;

import lombok.RequiredArgsConstructor;

/**
 * ProductRepositoryインターフェイス実装のjOOQ
 */
@Repository
@RequiredArgsConstructor
public class ProductJooqRepository implements ProductRepository{

    /** 
     * jOOQ のクエリ実行を担う DSLContext 
     */
    private final DSLContext dsl;

    /**
     * Record6 -> Product の変換（MapStruct生成Bean）
     */
    private final ProductRecordMapper mapper;

    /**
     * 新しい商品を永続化する
     * @param product 永続化する商品
     */
    @Override
    public void create(Product product) {
        if (product == null) throw new DomainException("商品は必須です。");
        try {
            // 商品カテゴリのUUIDから主キー値を取得する
            Integer categoryPk = dsl.select(ProductCategoryTable.PRODUCT_CATEGORY.ID)
                .from(ProductCategoryTable.PRODUCT_CATEGORY)
                .where(ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID.eq(UUID.fromString(product.getCategoryId().value())))
                .fetchOneInto(Integer.class);
            if (categoryPk == null) {
                throw new DomainException("指定された商品カテゴリが存在しません。");
            }    
            // 新しい商品を追加し、主キー値を受け取る
            Integer productPk = dsl.insertInto(ProductTable.PRODUCT)
                .set(ProductTable.PRODUCT.PRODUCT_UUID, UUID.fromString(product.getProductId().value()))
                .set(ProductTable.PRODUCT.NAME,  product.getName().value())
                .set(ProductTable.PRODUCT.PRICE, product.getPrice().value())
                .set(ProductTable.PRODUCT.CATEGORY_ID, categoryPk)
                .returning(ProductTable.PRODUCT.ID)
                .fetchOne()
                .getId();
            // 商品在庫を追加する
            dsl.insertInto(ProductStockTable.PRODUCT_STOCK)
               .set(ProductStockTable.PRODUCT_STOCK.PRODUCT_ID, productPk)
               .set(ProductStockTable.PRODUCT_STOCK.STOCK, product.currentStock().value())
               .execute();
        }catch (DataAccessException ex) {
            throw new InternalException("商品登録中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            if (ex instanceof DomainException) throw (DomainException) ex;
            throw new InternalException("商品登録処理中に予期しないエラーが発生しました。", ex);
        }
    }

    /**
     * 指定された商品名が存在有無を返す
     * @param productName 商品名
     * @return true:存在する false:存在しない
     */
    @Override
    public Boolean existsByName(ProductName productName) {
        if (productName == null) {
            throw new DomainException("商品名は必須です。");
        }
        try {
            return dsl.fetchExists(
            dsl.selectOne()
               .from(ProductTable.PRODUCT)
               .where(ProductTable.PRODUCT.NAME.eq(productName.value())));
        }  catch (org.jooq.exception.DataAccessException ex) {
            throw new InternalException("商品名の存在確認中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品名の存在確認処理中に予期しないエラーが発生しました。", ex);
        }
    }

    /**
     * 指定された商品Idの商品を取得する
     * @param categoryId 商品Id(VO)
     * @return 
     *  - 存在する場合: Productエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    @Override
    public Optional<Product> findById(ProductId productId) {
        if (productId == null) {
            throw new DomainException("商品Idは必須です。");
        }
        try {
            // ProductテーブルとStockテーブルをJOINして取得
            UUID uuid = UUID.fromString(productId.value());
            Record6<UUID, String, Integer, UUID, Integer, UUID> rec = dsl
            .select(
                ProductTable.PRODUCT.PRODUCT_UUID,
                ProductTable.PRODUCT.NAME,
                ProductTable.PRODUCT.PRICE,
                ProductStockTable.PRODUCT_STOCK.STOCK_UUID,
                ProductStockTable.PRODUCT_STOCK.STOCK,
                ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID
            )
            .from(ProductTable.PRODUCT)
            .join(ProductStockTable.PRODUCT_STOCK)
            .on(ProductTable.PRODUCT.ID.eq(ProductStockTable.PRODUCT_STOCK.PRODUCT_ID))
            .join(ProductCategoryTable.PRODUCT_CATEGORY)
            .on(ProductTable.PRODUCT.CATEGORY_ID.eq(ProductCategoryTable.PRODUCT_CATEGORY.ID))
            .where(ProductTable.PRODUCT.PRODUCT_UUID.eq(uuid))
            .fetchOne();
            // 該当なし → Optional.empty()
            return Optional.ofNullable(rec).map(mapper::toDomain);
        }catch (DataAccessException ex) {
            throw new InternalException("商品情報の取得中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品情報の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }

    /** 
     * 商品名で商品を取得する
     * @param productName 商品名（VO）
     *  - 存在する場合: Productエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    @Override
    public Optional<Product> findByName(ProductName productName) {
        if (productName == null) {
            throw new DomainException("商品名は必須です。");
        }
        try {
            Record6<UUID, String, Integer, UUID, Integer, UUID> rec = dsl
            .select(
                ProductTable.PRODUCT.PRODUCT_UUID,
                ProductTable.PRODUCT.NAME,
                ProductTable.PRODUCT.PRICE,
                ProductStockTable.PRODUCT_STOCK.STOCK_UUID,
                ProductStockTable.PRODUCT_STOCK.STOCK,
                ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID
            )
            .from(ProductTable.PRODUCT)
            .join(ProductStockTable.PRODUCT_STOCK)
                .on(ProductTable.PRODUCT.ID.eq(ProductStockTable.PRODUCT_STOCK.PRODUCT_ID))
            .join(ProductCategoryTable.PRODUCT_CATEGORY)
                .on(ProductTable.PRODUCT.CATEGORY_ID.eq(ProductCategoryTable.PRODUCT_CATEGORY.ID))
            .where(ProductTable.PRODUCT.NAME.eq(productName.value()))
            .fetchOne();
             // 該当なし → Optional.empty()
            return Optional.ofNullable(rec).map(mapper::toDomain);
        }catch (DataAccessException ex) {
            throw new InternalException("商品名による検索中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品名による検索処理中に予期しないエラーが発生しました。", ex);
        }
    }
}

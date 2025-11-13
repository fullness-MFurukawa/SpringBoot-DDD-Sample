package com.example.ddd_demo.infrastructure.persistence.product;

import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
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
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductCategoryRecord;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductRecord;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductStockRecord;

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
     * Product集約の「合成（Record → 集約）」および「分解（集約 → 個別Entity/Record）」を担うアセンブラ
     */
    private final ProductAssembler assembler;
 
    /**
     * 新しい商品を永続化する
     * @param product 永続化する商品
     */
    @Override
    public void create(Product product) {
        if (product == null) throw new DomainException("商品は必須です。");
        try {
            // カテゴリUUID → カテゴリの内部PK(INT)を解決
            UUID categoryUuid = UUID.fromString(assembler.extractCategoryUuid(product));
            // 商品カテゴリのUUIDから主キー値を取得する
            Integer categoryPk = dsl
                .select(ProductCategoryTable.PRODUCT_CATEGORY.ID)
                .from(ProductCategoryTable.PRODUCT_CATEGORY)
                .where(ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID.eq(categoryUuid))
                .fetchOneInto(Integer.class);
            if (categoryPk == null) {
                throw new DomainException("指定された商品カテゴリが存在しません。");
            }
            // 集約からRecordを生成(外部キー未設定)
            ProductRecord pr         = assembler.toProductRecord(product);
            ProductStockRecord sr    = assembler.toStockRecord(product);
            
            // ProductRecordにcategory_idを補完して追加(主キー採番を受け取る)
            pr.setCategoryId(categoryPk);
            Integer productPk = dsl.insertInto(ProductTable.PRODUCT)
                .set(pr)                                
                .returning(ProductTable.PRODUCT.ID)
                .fetchOne()
                .getId();

            // StockRecordにproduct_idを補完して追加
            sr.setProductId(productPk);
            dsl.insertInto(ProductStockTable.PRODUCT_STOCK)
                .set(sr)
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
        }  catch (DataAccessException ex) {
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
            // 各テーブルの全カラムを選択してJOINし、1行取得
            var rec = dsl
                .select(ProductTable.PRODUCT.fields())                 // Product 用
                .select(ProductStockTable.PRODUCT_STOCK.fields())      // Stock 用
                .select(ProductCategoryTable.PRODUCT_CATEGORY.fields())// Category 用
                .from(ProductTable.PRODUCT)
                .join(ProductStockTable.PRODUCT_STOCK)
                    .on(ProductTable.PRODUCT.ID.eq(ProductStockTable.PRODUCT_STOCK.PRODUCT_ID))
                .join(ProductCategoryTable.PRODUCT_CATEGORY)
                    .on(ProductTable.PRODUCT.CATEGORY_ID.eq(ProductCategoryTable.PRODUCT_CATEGORY.ID))
                .where(ProductTable.PRODUCT.PRODUCT_UUID.eq(uuid))
                .fetchOne();
            // 該当なし → Optional.empty()
            if (rec == null){
                return Optional.empty();
            }
            // 生成RecordへマッピングしてAssemblerで合成
            ProductRecord pr           = rec.into(ProductRecord.class);
            ProductStockRecord sr      = rec.into(ProductStockRecord.class);
            ProductCategoryRecord cr   = rec.into(ProductCategoryRecord.class);
            return Optional.of(assembler.assemble(pr, cr, sr));
        }catch (DataAccessException ex) {
            throw new InternalException("商品情報の取得中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品情報の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }

    /** 
     * 商品名で商品を取得する
     * @param productName 商品名(VO)
     *  - 存在する場合: Productエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    @Override
    public Optional<Product> findByName(ProductName productName) {
        if (productName == null) {
            throw new DomainException("商品名は必須です。");
        }
        try {
            // 各テーブルの“全カラム”を選択しておくと、into(ProductRecord.class) 等が安全に使える
            var rec = dsl
                .select(ProductTable.PRODUCT.fields())                // Product 用
                .select(ProductStockTable.PRODUCT_STOCK.fields())     // Stock   用
                .select(ProductCategoryTable.PRODUCT_CATEGORY.fields())// Category用
                .from(ProductTable.PRODUCT)
                .join(ProductStockTable.PRODUCT_STOCK)
                    .on(ProductTable.PRODUCT.ID.eq(ProductStockTable.PRODUCT_STOCK.PRODUCT_ID))
                .join(ProductCategoryTable.PRODUCT_CATEGORY)
                    .on(ProductTable.PRODUCT.CATEGORY_ID.eq(ProductCategoryTable.PRODUCT_CATEGORY.ID))
                .where(ProductTable.PRODUCT.NAME.eq(productName.value()))
                .fetchOne();
            // 該当なし → Optional.empty()
            if (rec == null){
                return Optional.empty();
            }
            // レコード分解 → 生成Recordにマッピング → Assemblerで合成
            ProductRecord pr = rec.into(ProductRecord.class);
            ProductStockRecord sr = rec.into(ProductStockRecord.class);
            ProductCategoryRecord cr = rec.into(ProductCategoryRecord.class);
            return Optional.of(assembler.assemble(pr, cr, sr));
            //return Optional.ofNullable(rec).map(mapper::toDomain);
        }catch (DataAccessException ex) {
            throw new InternalException("商品名による検索中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品名による検索処理中に予期しないエラーが発生しました。", ex);
        }
    }
}

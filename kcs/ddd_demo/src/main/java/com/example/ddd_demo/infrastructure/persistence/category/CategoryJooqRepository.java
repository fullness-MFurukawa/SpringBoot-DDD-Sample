package com.example.ddd_demo.infrastructure.persistence.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.mapper.ToDomainMapper;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryRepository;
import com.example.ddd_demo.infrastructure.exception.InternalException;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductCategoryTable;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductCategoryRecord;

import lombok.RequiredArgsConstructor;

/**
 * CategoryRepositoryインターフェイス実装のjOOQ
 */
@Repository
@RequiredArgsConstructor
public class CategoryJooqRepository implements CategoryRepository {

    /** 
     * jOOQ のクエリ実行を担う DSLContext 
     */
    private final DSLContext dsl;
    
    /**
     * jOOQのRecordからCategoryエンティティを再構築するMapper
     */
    private final ToDomainMapper<ProductCategoryRecord, Category> mapper;

    /**
     * 指定された商品カテゴリIdのカテゴリを取得する
     * @param categoryId 商品カテゴリId(VO)
     * @return 
     *  - 存在する場合: Categoryエンティティを保持する Optional  
     *  - 存在しない場合: 空のOptional(Optional.empty())
     */
    @Override
    public Optional<Category> findById(CategoryId categoryId) {
        if (categoryId == null) throw new DomainException("商品カテゴリIdは必須です。");
        // CategoryIdはString型なのでUUIDに変換
        try {
            UUID uuid = java.util.UUID.fromString(categoryId.value());
            return dsl.selectFrom(ProductCategoryTable.PRODUCT_CATEGORY)
                  .where(ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID.eq(uuid))
                  .fetchOptional()                 
                  .map(mapper::toDomain);  
        }catch (DataAccessException ex) {
            // jOOQ 由来のDBアクセス例外 → 内部例外としてラップ
            throw new InternalException("カテゴリ情報の取得中にデータベースエラーが発生しました。", ex);

        } catch (Exception ex) {
            // 想定外の例外も捕捉してラップ
            throw new InternalException("カテゴリ情報の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }

    /**
     * すべての商品カテゴリを取得する
     * @return すべての商品カテゴリを持つリスト
     */
    @Override
    public List<Category> findAll() {
        try {
            // すべての商品カテゴリを取得してCategoryエンティティを再構築して返す
            return dsl.selectFrom(ProductCategoryTable.PRODUCT_CATEGORY)
                  .orderBy(ProductCategoryTable.PRODUCT_CATEGORY.ID.asc())
                  .fetch(mapper::toDomain);   // mapper: ProductCategoryRecord -> Category
        }catch (DataAccessException ex) {
            throw new InternalException("カテゴリ一覧の取得中にデータベースエラーが発生しました。", ex);

        } catch (Exception ex) {
            throw new InternalException("カテゴリ一覧の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }    
}

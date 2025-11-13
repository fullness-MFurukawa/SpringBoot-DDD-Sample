package com.example.ddd_demo.infrastructure.persistence.category;

import org.mapstruct.Mapper;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.mapper.ToDomainMapper;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.domain.models.category.CategoryId;
import com.example.ddd_demo.domain.models.category.CategoryName;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductCategoryTable;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductCategoryRecord;

/**
 * jOOQが生成した {@link ProductCategoryRecord}から、
 * ドメインエンティティ {@link Category} を再構築(マッピング)するためのMapper。
 *
 * <p>このクラスは DDD における「腐敗防止層（Anti-Corruption Layer）」として機能し、
 * jOOQの永続化レコード(外部データ構造)を、 ドメインモデルが理解できる型
 * ({@link CategoryId}, {@link CategoryName} を持つ {@code Category})に変換します。
 * <br>これにより、アプリケーション層はデータベース構造やjOOQの内部実装を直接意識する必要がなくなります。
 *
 * <p><b>設計意図：</b><br>
 * - 永続化技術（jOOQ）とドメインモデルの疎結合を保つ。<br>
 * - ドメインの語彙(CategoryId, CategoryName)で表現された正しい状態のエンティティだけを生成する。<br>
 * - データ不整合や欠損があった場合には{@link DomainException}を送出し、早期に異常を検知する。
 *
 * <p><b>利用例：</b>
 * <pre>{@code
 * ProductCategoryRecord record = dsl
 *     .selectFrom(ProductCategoryTable.PRODUCT_CATEGORY)
 *     .where(ProductCategoryTable.PRODUCT_CATEGORY.ID.eq(1))
 *     .fetchOneInto(ProductCategoryRecord.class);
 *
 * Category category = mapper.toDomain(record);
 * }</pre>
 *
 * <p><b>対応するテーブル：</b><br>
 * {@link ProductCategoryTable#PRODUCT_CATEGORY}
 *
 * <p>MapStruct により Spring 管理下で利用されるため、
 * {@code @Mapper(componentModel = "spring")} を指定しています。
 *
 * @see ToDomainMapper
 * @see Category
 * @see DomainException
 */
@Mapper(componentModel = "spring")
public interface CategoryRecordMapper extends ToDomainMapper<ProductCategoryRecord, Category>{
    /**
     * jOOQの{@link ProductCategoryRecord}を{@link Category}エンティティに変換します。
     *
     * <p>変換時に以下の検証を行い、ドメインルールを満たさない場合は{@link DomainException}をスローします。
     * <ul>
     *   <li>カテゴリUUIDが null でないこと</li>
     *   <li>カテゴリ名が null または空文字でないこと</li>
     * </ul>
     *
     * @param input jOOQ により取得された {@link ProductCategoryRecord}
     * @return 検証済みの{@link Category}エンティティ
     * @throws DomainException カラム値がnullまたは不正形式の場合
     */
    default Category toDomain(ProductCategoryRecord input) {
        if (input == null){
            throw new DomainException("カテゴリ情報が取得できません。");
        } 
        var categoryUuid = input.getCategoryUuid();
        var name = input.getName();
        if (categoryUuid == null) {
            throw new DomainException("カテゴリUUIDが不正です。");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("カテゴリ名が未設定です。");
        }
        // ProductCategoryRecordからCategoryを再構築する
        return Category.restore(
            CategoryId.fromString(categoryUuid.toString()), 
            CategoryName.of(name));
    }
}

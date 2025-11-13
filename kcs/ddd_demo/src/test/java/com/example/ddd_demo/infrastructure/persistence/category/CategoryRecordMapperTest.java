package com.example.ddd_demo.infrastructure.persistence.category;

import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.records.ProductCategoryRecord;

/**
 * jOOQの生成レコード(ProductCategoryRecord)からCategoryエンティティを再構築する
 * MapStruct版CategoryRecordMapperの単体テストドライバ
 */
// MapStruct が生成する実装クラスだけを読み込む
@ContextConfiguration(classes = { CategoryRecordMapperImpl.class })
@SpringBootTest
public class CategoryRecordMapperTest {

    /** テスト対象（MapStructの生成Bean） */
    @Autowired
    private CategoryRecordMapper mapper;

    /** 
     * テスト用のProductCategoryRecordを生成 
     */
    private ProductCategoryRecord createRecord(UUID categoryUuid, String name) {
        var rec = new ProductCategoryRecord();
        rec.setCategoryUuid(categoryUuid);
        rec.setName(name);
        return rec;
    }

    @Nested
    class 正常系 {
        @Test
        @DisplayName("Record2からCategoryエンティティを正しく再構築できる")
        void toDomain_success() {
            UUID uuid = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
             ProductCategoryRecord record = createRecord(uuid, "文房具");

            Category category = mapper.toDomain(record);

            assertThat(category.getCategoryId().value()).isEqualTo(uuid.toString());
            assertThat(category.getName().value()).isEqualTo("文房具");
        }
    }

    @Nested
    class 異常系 {
        @Test
        @DisplayName("カテゴリ名が空白のみの場合はDomainExceptionをスローする")
        void invalid_name_blank() {
            ProductCategoryRecord record = createRecord(UUID.randomUUID(), "   ");
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("カテゴリ名");
        }

        @Test
        @DisplayName("カテゴリUUIDがnullの場合はDomainExceptionをスローする")
        void invalid_uuid_null() {
            ProductCategoryRecord record = createRecord(null, "雑貨");
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("変換対象がnullの場合はDomainExceptionをスローする")
        void null_record() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(DomainException.class);
        }
    }
}

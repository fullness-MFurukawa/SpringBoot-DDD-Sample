package com.example.ddd_demo.infrastructure.persistence.category;

import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ddd_demo.domain.exception.DomainException;
import com.example.ddd_demo.domain.models.category.Category;
import com.example.ddd_demo.infrastructure.persistence.schema.tables.ProductCategoryTable;

/**
 * jOOQのRecordからCategoryエンティティを再構築する
 * MapStruct版CategoryRecordMapperの単体テストドライバ
 */
@SpringBootTest
public class CategoryRecordMapperTest {

    @Autowired
    private DSLContext dsl;

    /** テスト対象（MapStructの生成Bean） */
    @Autowired
    private CategoryRecordMapper mapper;

    /** 
     * テスト用のRecord2を生成 
     **/
    private Record2<UUID, String> createRecord(UUID categoryUuid, String name) {
        var rec = dsl.newRecord(
            ProductCategoryTable.PRODUCT_CATEGORY.CATEGORY_UUID,
            ProductCategoryTable.PRODUCT_CATEGORY.NAME
        );
        rec.values(categoryUuid, name);
        return rec;
    }

    @Nested
    class 正常系 {
        @Test
        @DisplayName("Record2からCategoryエンティティを正しく再構築できる")
        void toDomain_success() {
            UUID uuid = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
            Record2<UUID, String> record = createRecord(uuid, "文房具");

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
            Record2<UUID, String> record = createRecord(UUID.randomUUID(), "   ");
            assertThatThrownBy(() -> mapper.toDomain(record))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("カテゴリ名");
        }

        @Test
        @DisplayName("カテゴリUUIDがnullの場合はDomainExceptionをスローする")
        void invalid_uuid_null() {
            Record2<UUID, String> record = createRecord(null, "雑貨");
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

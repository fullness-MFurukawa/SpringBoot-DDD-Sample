package com.example.ddd_demo.domain.models.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * 商品カテゴリエンティティ:Categoryの単体テストドライバ
 * - 生成/再構築、リネーム、例外、不変条件、等価性(hashCode含む)を検証
 */
@DisplayName("Categoryエンティティの単体テスト")
public class CategoryTest {
    // ヘルパー: 値オブジェクト
    private static CategoryName name(String s) { return CategoryName.of(s); }
    private static CategoryId   id(String s)   { return CategoryId.fromString(s); }

    @Test
    @DisplayName("createNew(): 新規作成で識別子は自動発番され、名称が設定される")
    void createNew_ShouldGenerateId_AndKeepName() {
        // エンティティを生成する
        Category c = Category.createNew(name("文房具"));

        // 商品カテゴリIdはnullではない
        assertNotNull(c.getCategoryId());
        // 商品カテゴリ名が設定されている
        assertEquals("文房具", c.getName().value());
    }

    @Test
    @DisplayName("createNew(): 商品カテゴリ名がnullならDomainExceptionをスローする")
    void createNew_NullName_ShouldThrow() {
        assertThrows(DomainException.class, () -> Category.createNew(null));
    }


    @Test
    @DisplayName("rehydrate(): 指定した識別子と名称でエンティティを再構築できる")
    void rehydrate_ShouldBuildEntityWithGivenIdAndName() {
        // 商品カテゴリId
        CategoryId existingId = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        // エンティティを生成
        Category c = Category.restore(existingId, name("雑貨"));
        // 商品Idが同じである
        assertEquals(existingId, c.getCategoryId());
        // 商品カテゴリ名が設定されている
        assertEquals("雑貨", c.getName().value());
    }

    @Test
    @DisplayName("rehydrate(): 商品カテゴリIdがnullならDomainExceptionがスローされる")
    void rehydrate_NullId_ShouldThrow() {
        assertThrows(DomainException.class, () 
            -> Category.restore(null, name("雑貨")));
    }

    @Test
    @DisplayName("rehydrate(): 商品カテゴリ名がnullならDomainExceptionがスローされる")
    void rehydrate_NullName_ShouldThrow() {
        CategoryId existingId = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        assertThrows(DomainException.class, () 
            -> Category.restore(existingId, null));
    }

    @Test
    @DisplayName("rename(): 新しい名称で更新できる")
    void rename_ShouldUpdateName_WhenValid() {
        Category c = Category.createNew(name("文房具"));
        // 商品カテゴリ名を変更
        c.rename(name("雑貨"));
        // 変更された値を保持している
        assertEquals("雑貨", c.getName().value());
    }

    @Test
    @DisplayName("rename(): 商品カテゴリ名がnullならDomainExceptionをスローする")
    void rename_Null_ShouldThrow() {
        Category c = Category.createNew(name("文房具"));
        // DomainExceptionがスローされたことを検証する
        DomainException ex = assertThrows(DomainException.class, () 
            -> c.rename(null));
        // エラーメッセージを検証する
        assertEquals("カテゴリ名は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("equals/hashCode: 同一のCategoryIdなら等価、異なれば非等価")
    void equalsHashCode_ShouldUseIdentity() {
        // 同一商品カテゴリIdで異なる商品カテゴリ名のインスタンスを生成する
        CategoryId sameId = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        Category a = Category.restore(sameId, name("文房具"));
        Category b = Category.restore(sameId, name("雑貨"));

        // 等価性（同一性）: 商品カテゴリIdが同じなら等価と評価される
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        // 別な商品カテゴリIdなら非等価
        Category c = Category.restore(
            id("8f81a72a-58ef-422b-b472-d982e8665292"),
            name("文房具"));
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("toString(): スモーク（例外なく意味のある文字列を返す）")
    void toString_Smoke() {
        Category c = Category.restore(
            id("ac413f22-0cf1-490a-9635-7e9ca810e544"),
            name("文房具"));
        String s = c.toString();

        // 返された文字列に含まれる値を検証する
        assertTrue(s.contains("Category"));
        assertTrue(s.contains("文房具"));
        assertTrue(s.contains("ac413f22-0cf1-490a-9635-7e9ca810e544"));
    }
}

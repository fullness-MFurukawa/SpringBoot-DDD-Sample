package com.example.ddd_demo.domain.models.category;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * CategoryId 値オブジェクトの単体テストドライバ
 * - UUID生成・復元・等価判定などの検証を行う
 */
@DisplayName("CategoryId 値オブジェクトの単体テスト")
public class CategoryIdTest {
    // UUIDのcanonical形式を表す正規表現
    private static final Pattern CANONICAL_UUID =
        Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    @Test
    @DisplayName("createNew(): 生成されるUUIDはcanonical形式(小文字・ハイフン付き36文字)である")
    void createNew_ShouldReturnCanonicalUuid() {
        // 実行
        CategoryId id = CategoryId.createNew();
        // nullではない
        assertNotNull(id);    
        // 値が設定されている                                      
        assertNotNull(id.value());   
        // 36文字
        assertEquals(36, id.value().length());             
        // 正しいUUID形式
        assertTrue(CANONICAL_UUID.matcher(id.value()).matches()); 
    }

    @Test
    @DisplayName("createNew(): 毎回異なるUUIDが生成される")
    void createNew_ShouldBeUnique() {
        // 値オブジェクトを生成する
        var a = CategoryId.createNew();
        var b = CategoryId.createNew();
        // オブジェクトが異なる
        assertNotEquals(a, b);                    
        // 値も異なる
        assertNotEquals(a.value(), b.value());
    }

    @Test
    @DisplayName("fromString(): 大文字でも受け付け、内部では小文字のcanonicalに正規化される")
    void fromString_ShouldNormalizeToLowerCanonical() {
        // 大文字のUUID
        var rawUpper = "AC413F22-0CF1-490A-9635-7E9CA810E544";
        // 既存のUUID 文字列からCategoryIdを復元する
        var id = CategoryId.fromString(rawUpper);
        // 内部では小文字に正規化されることを検証する
        assertEquals(rawUpper.toLowerCase(Locale.ROOT), id.value());
        assertTrue(CANONICAL_UUID.matcher(id.value()).matches());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    @DisplayName("fromString(): 空・空白のみはDomainException")
    void fromString_Blank_ShouldThrow(String raw) {
        // 空文字や空白のみは例外をスロー
        assertThrows(DomainException.class, () -> CategoryId.fromString(raw));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "not-uuid",                                       // UUID形式でない
        "0000000-0000-0000-0000-000000000000",            // 先頭ブロック7桁（不足）
        "00000000-0000-0000-0000-0000000000",             // 最終ブロック10桁（不足）
        "00000000-0000-0000-0000-0000000000000",          // 最終ブロック13桁（過多）
        "zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz"            // 16進以外
    })
    @DisplayName("fromString(): UUID形式でない場合はDomainException")
    void fromString_InvalidFormat_ShouldThrow(String raw) {
         // まず、このテストデータが“有効”に化けていないか自己チェック（誤検知防止）
        assertFalse(CANONICAL_UUID.matcher(raw).matches(),
            () -> "テストデータが有効なUUID形式になっています: [" + raw + "] (len=" + raw.length() + ")");

        // 不正なら必ずDomainExceptionをスローする
        assertThrows(DomainException.class, () -> CategoryId.fromString(raw));
    }

    @Test
    @DisplayName("equals/hashCode: 同じUUID文字列なら等価")
    void equalsHashCode_SameValue_ShouldBeEqual() {
        var raw = "ac413f22-0cf1-490a-9635-7e9ca810e544";

        // 同じUUID文字列で生成
        var a = CategoryId.fromString(raw);
        var b = CategoryId.fromString(raw);

        // 等価であることを確認
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("toString(): value() と同じcanonical文字列を返す")
    void toString_ShouldReturnCanonical() {
        var id = CategoryId.fromString("ac413f22-0cf1-490a-9635-7e9ca810e544");
        // toString()がvalue()と同一であることを確認
        assertEquals(id.value(), id.toString());
    }
}

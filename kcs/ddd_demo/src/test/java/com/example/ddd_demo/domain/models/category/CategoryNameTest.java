package com.example.ddd_demo.domain.models.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * CategoryName 値オブジェクトの単体テストドライバ
 * - 必須・最大長・等価判定などの仕様を検証する
 */
@DisplayName("CategoryName 値オブジェクトの単体テスト")
public class CategoryNameTest {
    
    @Test
    @DisplayName("of(): 正常系 - 前後空白はトリムされ、値が保持される")
    void of_ShouldTrimAndKeepValue() {
        // 入力値に前後空白を含むケース
        var name = CategoryName.of("  文房具  ");
        // トリムされて格納されていることを検証する
        assertEquals("文房具", name.value());
        assertEquals("文房具", name.toString());
    }

    @Test
    @DisplayName("of():同じ値なら等価（equals/hashCode）")
    void equalsHashCode_SameValue_ShouldBeEqual() {
        var a = CategoryName.of("雑貨");
        var b = CategoryName.of("雑貨");

        // 等価であり、ハッシュコードも一致する
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("of(): 異なる値なら非等価")
    void equalsHashCode_DifferentValue_ShouldNotEqual() {
        var a = CategoryName.of("雑貨");
        var b = CategoryName.of("文房具");
        // 異なる値は非等価
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("of(): nullはDomainExceptionをスローする")
    void of_Null_ShouldThrow() {
        // null入力はDomainExceptionをスローする
        assertThrows(DomainException.class, () -> CategoryName.of(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    @DisplayName("of(): 空や空白のみはDomainExceptionをスローする")
    void of_Blank_ShouldThrow(String raw) {
        // 空文字や空白のみの入力は例外
        assertThrows(DomainException.class, () -> CategoryName.of(raw));
    }

    @Test
    @DisplayName("of(): 最大長(20文字)を超えるとDomainExceptionをスローする")
    void of_TooLong_ShouldThrow() {
        // 21文字（上限超え）の文字列を作成
        String over = "あ".repeat(21);
        // 例外がスローされる
        assertThrows(DomainException.class, () -> CategoryName.of(over));
    }

    @Test
    @DisplayName("of(): ちょうど20文字は許可される")
    void of_MaxAllowed_ShouldPass() {
        // 上限ちょうど20文字
        String just = "あ".repeat(20);

        // 正常に生成できる
        var name = CategoryName.of(just);
        assertEquals(just, name.value());
    }
}

package com.example.ddd_demo.domain.models.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * ProductName 値オブジェクトの単体テストドライバ
 * - 必須・最大長・等価判定などの仕様を検証する
 */
@DisplayName("ProductName 値オブジェクトの単体テスト")
public class ProductNameTest {
    
    @Test
    @DisplayName("of(): 前後空白はトリムされ、値が保持される")
    void of_ShouldTrimAndKeepValue() {
        // 入力値に前後空白を含むケース
        var name = ProductName.of("  ノートパソコン  ");
        // トリムされて格納されていることを検証する
        assertEquals("ノートパソコン", name.value());
        assertEquals("ノートパソコン", name.toString());
    }

    @Test
    @DisplayName("of(): 同じ値なら等価（equals/hashCode）")
    void equalsHashCode_SameValue_ShouldBeEqual() {
        var a = ProductName.of("マウス");
        var b = ProductName.of("マウス");
        // 等価であり、ハッシュコードも一致する
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("of(): 異なる値なら非等価")
    void equalsHashCode_DifferentValue_ShouldNotEqual() {
        var a = ProductName.of("マウス");
        var b = ProductName.of("キーボード");
        // 異なる値は非等価
        assertNotEquals(a, b);
    }

    
    @Test
    @DisplayName("of(): nullはDomainExceptionをスローする")
    void of_Null_ShouldThrow() {
        // null入力はDomainExceptionをスローする
        assertThrows(DomainException.class, () 
            -> ProductName.of(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    @DisplayName("of(): 空や空白のみはDomainExceptionをスローする")
    void of_Blank_ShouldThrow(String raw) {
        // 空文字や空白のみの入力は例外
        assertThrows(DomainException.class, () 
            -> ProductName.of(raw));
    }

    @Test
    @DisplayName("of(): 最大長(30文字)を超えるとDomainExceptionをスローする")
    void of_TooLong_ShouldThrow() {
        // 31文字（上限超え）の文字列を作成
        String over = "あ".repeat(31);
        // 例外がスローされる
        assertThrows(DomainException.class, () -> ProductName.of(over));
    }

    @Test
    @DisplayName("of(): ちょうど30文字は許可される")
    void of_MaxAllowed_ShouldPass() {
        // 上限ちょうど30文字
        String just = "あ".repeat(30);
        // 正常に生成できる
        var name = ProductName.of(just);
        assertEquals(just, name.value());
    }
}

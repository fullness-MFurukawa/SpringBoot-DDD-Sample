package com.example.ddd_demo.domain.models.stock;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * StockQuantity 値オブジェクトの単体テストドライバ
 * - 必須・範囲チェック・等価判定などの仕様を検証する
 */
@DisplayName("StockQuantity 値オブジェクトの単体テスト")
public class StockQuantityTest {

    @Test
    @DisplayName("of(): 正常系 - 有効な値なら生成できる")
    void of_Valid_ShouldCreate() {
        var q = StockQuantity.of(50);
        assertEquals(50, q.value());
        assertEquals("50", q.toString());
    }

    @Test
    @DisplayName("of(): 同じ値なら等価（equals/hashCode）")
    void equalsHashCode_SameValue_ShouldBeEqual() {
        var a = StockQuantity.of(10);
        var b = StockQuantity.of(10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("of(): 異なる値なら非等価")
    void equalsHashCode_DifferentValue_ShouldNotEqual() {
        var a = StockQuantity.of(10);
        var b = StockQuantity.of(11);
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("of(): nullはDomainExceptionをスローする")
    void of_Null_ShouldThrow() {
        DomainException ex = assertThrows(DomainException.class, 
            () -> StockQuantity.of(null));
        assertEquals("在庫数は必須です。", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { -10, -1 })
    @DisplayName("of(): 0未満はDomainExceptionをスローする")
    void of_TooLow_ShouldThrow(int invalid) {
        assertThrows(DomainException.class, () -> StockQuantity.of(invalid));
    }

    @ParameterizedTest
    @ValueSource(ints = { 101, 150, Integer.MAX_VALUE })
    @DisplayName("of(): 100を超える値はDomainExceptionをスローする")
    void of_TooHigh_ShouldThrow(int invalid) {
        assertThrows(DomainException.class, () -> StockQuantity.of(invalid));
    }

    @Test
    @DisplayName("of(): 境界値0は許可される")
    void of_MinAllowed_ShouldPass() {
        var q = StockQuantity.of(0);
        assertEquals(0, q.value());
    }

    @Test
    @DisplayName("of(): 境界値100は許可される")
    void of_MaxAllowed_ShouldPass() {
        var q = StockQuantity.of(100);
        assertEquals(100, q.value());
    }
}

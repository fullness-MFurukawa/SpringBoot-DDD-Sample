package com.example.ddd_demo.domain.models.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * ProductPrice 値オブジェクトの単体テストドライバ
 * - 必須・範囲チェック・等価判定などの仕様を検証する
 */
@DisplayName("ProductPrice 値オブジェクトの単体テスト")
public class ProductPriceTest {

    @Test
    @DisplayName("of(): 有効な値なら生成できる")
    void of_ValidValue_ShouldCreate() {
        // 商品単価を生成する
        var price = ProductPrice.of(500);
        // 保持する値を検証する
        assertEquals(500, price.value());
        assertEquals("500", price.toString());
    }
    
    @Test
    @DisplayName("of(): 同じ値なら等価（equals/hashCode）")
    void equalsHashCode_SameValue_ShouldBeEqual() {
        var a = ProductPrice.of(1000);
        var b = ProductPrice.of(1000);
        // 等価であり、ハッシュコードも一致する
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("of(): 異なる値なら非等価")
    void equalsHashCode_DifferentValue_ShouldNotEqual() {
        var a = ProductPrice.of(100);
        var b = ProductPrice.of(200);
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("of(): nullはDomainExceptionをスローする")
    void of_Null_ShouldThrow() {
        assertThrows(DomainException.class, () 
            -> ProductPrice.of(null));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 10, 49 })
    @DisplayName("of(): 50未満はDomainExceptionをスローする")
    void of_TooLow_ShouldThrow(int invalid) {
        assertThrows(DomainException.class, () 
            -> ProductPrice.of(invalid));
    }

    @ParameterizedTest
    @ValueSource(ints = { 10001, 20000, Integer.MAX_VALUE })
    @DisplayName("of(): 10000を超える値はDomainExceptionをスローする")
    void of_TooHigh_ShouldThrow(int invalid) {
        assertThrows(DomainException.class, () -> ProductPrice.of(invalid));
    }

    @Test
    @DisplayName("of(): 最小値(50)は許可される")
    void of_MinAllowed_ShouldPass() {
        var price = ProductPrice.of(50);
        assertEquals(50, price.value());
    }

    @Test
    @DisplayName("of(): 最大値(10000)は許可される")
    void of_MaxAllowed_ShouldPass() {
        var price = ProductPrice.of(10000);
        assertEquals(10000, price.value());
    }
}

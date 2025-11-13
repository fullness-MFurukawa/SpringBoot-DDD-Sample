package com.example.ddd_demo.domain.models.stock;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.ddd_demo.domain.exception.DomainException;

/**
 * 商品在庫エンティティ:Stockの単体テストドライバ
 * - 生成/再構築、リネーム、例外、不変条件、等価性(hashCode含む)を検証
 */
@DisplayName("Stock エンティティの単体テスト")
public class StockTest {
    /**
     * ヘルパー
     */
    private static StockQuantity q(int v) { return StockQuantity.of(v); }
    private static StockId id(String s)   { return StockId.fromString(s); }

    @Test
    @DisplayName("createNew(): 初期在庫が設定され、IDは自動発番される")
    void createNew_ShouldGenerateId_AndKeepQuantity() {
        Stock s = Stock.createNew(q(10));
        assertNotNull(s.getStockId());
        assertEquals(10, s.getQuantity().value());
    }

    
    @Test
    @DisplayName("createNew(): 初期在庫がnullならDomainException（メッセージ検証）")
    void createNew_NullQuantity_ShouldThrow() {
        DomainException ex = assertThrows(DomainException.class, () -> Stock.createNew(null));
        assertEquals("在庫数は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("rehydrate(): 指定した在庫Idと在庫数で再構築できる")
    void rehydrate_ShouldBuildWithGivenIdAndQuantity() {
        StockId existing = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        Stock s = Stock.restore(existing, q(20));
        assertEquals(existing, s.getStockId());
        assertEquals(20, s.getQuantity().value());
    }

    @Test
    @DisplayName("rehydrate(): 在庫IdがnullならDomainExceptionをスローする")
    void rehydrate_NullId_ShouldThrow() {
        DomainException ex = assertThrows(DomainException.class,
            () -> Stock.restore(null, q(10)));
        assertEquals("在庫IDは必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("rehydrate(): 在庫数がnullならDomainExceptionをスローする")
    void rehydrate_NullQuantity_ShouldThrow() {
        StockId existing = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        DomainException ex = assertThrows(DomainException.class, 
            () -> Stock.restore(existing, null));
        assertEquals("在庫数は必須です。", ex.getMessage());
    }

    
    @Test
    @DisplayName("increase(): 0以上の加算で在庫が増える(境界内)")
    void increase_ShouldAdd_WhenWithinRange() {
        Stock s = Stock.createNew(q(10));
        s.increase(5);
        assertEquals(15, s.getQuantity().value());

        s.increase(85);  // 15 + 85 = 100（上限ちょうど）
        assertEquals(100, s.getQuantity().value());
    }

    @Test
    @DisplayName("increase(): 負の値はDomainExceptionをスローする")
    void increase_Negative_ShouldThrow() {
        Stock s = Stock.createNew(q(10));
        DomainException ex = assertThrows(DomainException.class, 
            () -> s.increase(-1));
        assertTrue(ex.getMessage().startsWith("在庫の増分は0以上で指定してください。"));
    }

    @Test
    @DisplayName("increase(): 上限(100)を超える加算はDomainExceptionをスローする")
    void increase_OverMax_ShouldThrow() {
        Stock s = Stock.createNew(q(90));
        assertThrows(DomainException.class, () -> s.increase(11)); // 101で弾かれる
    }

    @Test
    @DisplayName("decrease(): 0以上の減算で在庫が減る（境界内）")
    void decrease_ShouldSubtract_WhenWithinRange() {
        Stock s = Stock.createNew(q(10));
        s.decrease(3);
        assertEquals(7, s.getQuantity().value());

        s.decrease(7);  // 7 - 7 = 0（下限ちょうど）
        assertEquals(0, s.getQuantity().value());
    }

    @Test
    @DisplayName("decrease(): 負の値はDomainExceptionをスローする")
    void decrease_Negative_ShouldThrow() {
        Stock s = Stock.createNew(q(10));
        DomainException ex = assertThrows(DomainException.class, 
            () -> s.decrease(-1));
        assertTrue(ex.getMessage().startsWith("在庫の減分は0以上で指定してください。"));
    }

    @Test
    @DisplayName("decrease(): 下限(0)を下回る減算はDomainExceptionをスローする")
    void decrease_BelowMin_ShouldThrow() {
        Stock s = Stock.createNew(q(5));
        assertThrows(DomainException.class, () -> s.decrease(6)); // -1で弾かれる
    }

    @Test
    @DisplayName("changeQuantity(): 有効な在庫数に置き換えられる")
    void changeQuantity_ShouldReplace() {
        Stock s = Stock.createNew(q(10));
        s.changeQuantity(q(100));
        assertEquals(100, s.getQuantity().value());
    }

    @Test
    @DisplayName("changeQuantity(): nullはDomainExceptionをスローする")
    void changeQuantity_Null_ShouldThrow() {
        Stock s = Stock.createNew(q(10));
        DomainException ex = assertThrows(DomainException.class, () -> s.changeQuantity(null));
        assertEquals("在庫数は必須です。", ex.getMessage());
    }

    @Test
    @DisplayName("isOutOfStock(): 在庫0でtrue、それ以外はfalse")
    void isOutOfStock_ShouldReflectQuantity() {
        assertTrue(Stock.createNew(q(0)).isOutOfStock());
        assertFalse(Stock.createNew(q(1)).isOutOfStock());
    }

    @Test
    @DisplayName("isFullCapacity(): 在庫100でtrue、それ以外はfalse")
    void isFullCapacity_ShouldReflectQuantity() {
        assertTrue(Stock.createNew(q(100)).isFullCapacity());
        assertFalse(Stock.createNew(q(99)).isFullCapacity());
    }

    @Test
    @DisplayName("equals/hashCode: 同一StockIdなら等価、異なれば非等価")
    void equalsHashCode_ShouldUseIdentity() {
        StockId same = id("ac413f22-0cf1-490a-9635-7e9ca810e544");
        Stock a = Stock.restore(same, q(10));
        Stock b = Stock.restore(same, q(99));
        Stock c = Stock.restore(id("8f81a72a-58ef-422b-b472-d982e8665292"), q(10));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("toString(): スモーク（意味のある文字列が返る）")
    void toString_Smoke() {
        Stock s = Stock.restore(
                id("ac413f22-0cf1-490a-9635-7e9ca810e544"),
                q(10));
        String str = s.toString();
        assertTrue(str.contains("Stock"));
        assertTrue(str.contains("ac413f22-0cf1-490a-9635-7e9ca810e544"));
        assertTrue(str.contains("10"));
    }

}

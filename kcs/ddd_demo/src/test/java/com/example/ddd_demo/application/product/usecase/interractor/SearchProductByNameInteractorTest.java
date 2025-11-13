package com.example.ddd_demo.application.product.usecase.interractor;
    import static org.assertj.core.api.Assertions.*;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import com.example.ddd_demo.application.exception.NotFoundException;
    import com.example.ddd_demo.application.product.usecase.interactor.SearchProductByNameInteractor;
/**
 * ユースケース:[商品を名前で検索する]を実現するインターフェイス実装のテストドライバ
 */
@SpringBootTest
public class SearchProductByNameInteractorTest {
    /**
     * テストターゲット
     */
    @Autowired
    private SearchProductByNameInteractor interactor;

    @Test
    @DisplayName("search(): 既存の商品名で検索でき、DTOが正しく返る")
    void search_success() {
        var dto = interactor.search("蛍光ペン(赤)");
        // 商品Idを検証する
        assertThat(dto.getId()).isEqualTo("83fbc81d-2498-4da6-b8c2-54878d3b67ff");
        // 商品名を検証する
        assertThat(dto.getName()).isEqualTo("蛍光ペン(赤)");
        // 商品単価を検証する
        assertThat(dto.getPrice()).isEqualTo(130);  
        // 商品カテゴリがnullでないことを検証する
        assertThat(dto.getCategory()).isNotNull();
        // 商品カテゴリIdを検証する
        assertThat(dto.getCategory().getId()).isEqualTo("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4");
        // 商品在庫がnullでないことを検証する
        assertThat(dto.getStock()).isNotNull();
        // 商品在庫を検証する
        assertThat(dto.getStock().getQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("search(): 存在しない商品名ならNotFoundExceptionをスローする")
    void search_not_found() {
        assertThatThrownBy(() -> interactor.search("ペーパーナイフ"))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("商品名:[ペーパーナイフ]の商品は存在しません。");
    }
}

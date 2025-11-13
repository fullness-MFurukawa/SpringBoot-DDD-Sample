package com.example.ddd_demo.presentation.controller.product;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.dto.StockDTO;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.product.usecase.SearchProductByNameUsecase;
import com.example.ddd_demo.presentation.advice.ApiExceptionHandler;
import com.example.ddd_demo.presentation.product.controller.SearchProductByNameController;


/**
 * 商品検索コントローラのMockMVCテストドライバ
 */
@WebMvcTest(controllers = SearchProductByNameController.class)
@Import(ApiExceptionHandler.class) // 404/400/500など共通ハンドラを有効にする
public class SearchProductByNameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Controllerが依存するユースケースをモック
    @MockitoBean
    private SearchProductByNameUsecase usecase; 

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET /api/products/search?name=蛍光ペン(赤)：200 OKを返す")
    void search_success() throws Exception {
        var dto = new ProductDTO(
            "83fbc81d-2498-4da6-b8c2-54878d3b67ff",
            "蛍光ペン(赤)",
            130,
            new CategoryDTO("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具"),
            new StockDTO("11111111-2222-3333-4444-555555555555", 100)
        );
        when(usecase.search("蛍光ペン(赤)")).thenReturn(dto);

        mockMvc.perform(get("/api/products/search")
                .param("name", "蛍光ペン(赤)")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("蛍光ペン(赤)"))
            .andExpect(jsonPath("$.price").value(130))
            .andExpect(jsonPath("$.category.name").value("文房具"))
            .andExpect(jsonPath("$.stock.quantity").value(100));
    }

    @Test
    @DisplayName("GET /api/products/search?name=存在しない：404を返す")
    void search_notFound() throws Exception {
        when(usecase.search("存在しない"))
            .thenThrow(new NotFoundException("商品名:[存在しない]の商品は存在しません。"));

        mockMvc.perform(get("/api/products/search")
                .param("name", "存在しない"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products/search?name= (空)：400を返す")
    void search_blankName() throws Exception {
        mockMvc.perform(get("/api/products/search")
                .param("name", " "))
            .andExpect(status().isBadRequest());
    }
}

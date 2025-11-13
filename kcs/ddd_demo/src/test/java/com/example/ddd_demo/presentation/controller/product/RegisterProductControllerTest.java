package com.example.ddd_demo.presentation.controller.product;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import com.example.ddd_demo.application.exception.ExistsException;
import com.example.ddd_demo.application.exception.NotFoundException;
import com.example.ddd_demo.application.product.usecase.RegisterProductUsecase;
import com.example.ddd_demo.presentation.advice.ApiExceptionHandler;
import com.example.ddd_demo.presentation.product.controller.RegisterProductController;
import com.example.ddd_demo.presentation.product.schema.ProductCreateSchemaMapperImpl;

/**
 * 商品登録コントローラのMockMVCテストドライバ
 */
@WebMvcTest(controllers = RegisterProductController.class) 
@Import({ApiExceptionHandler.class,ProductCreateSchemaMapperImpl.class}) // 404/400/500など共通ハンドラを有効にする
public class RegisterProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Controllerが依存するユースケースをモック
    @MockitoBean
    private RegisterProductUsecase usecase; 


    @SuppressWarnings("null")
    @Test
    @DisplayName("GET /api/products/categories：200 & JSON配列")
    void getCategories_ok() throws Exception {
        var list = List.of(
            new CategoryDTO("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具"),
            new CategoryDTO("4c2bde9b-7e67-49ce-93a0-79c9f1b0a6b1", "雑貨"),
            new CategoryDTO("9c7a7a42-1e44-45ed-b9d7-4df90f09a0b1", "パソコン周辺機器")
        );
        given(usecase.getCategories()).willReturn(list);

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4"))
                .andExpect(jsonPath("$[0].name").value("文房具"))
                .andExpect(jsonPath("$[1].id").value("4c2bde9b-7e67-49ce-93a0-79c9f1b0a6b1"))
                .andExpect(jsonPath("$[1].name").value("雑貨"))
                .andExpect(jsonPath("$[2].id").value("9c7a7a42-1e44-45ed-b9d7-4df90f09a0b1"))
                .andExpect(jsonPath("$[2].name").value("パソコン周辺機器"));
    }

    @Nested
    class GetCategoryById {

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /api/products/categories/{id}：200")
        void getCategory_ok() throws Exception {
            var dto = new CategoryDTO("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具");
            given(usecase.getCategoryById(dto.getId())).willReturn(dto);

            mockMvc.perform(get("/api/products/categories/{id}", dto.getId()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.id").value(dto.getId()))
                   .andExpect(jsonPath("$.name").value("文房具"));
        }

        @Test
        @DisplayName("GET /api/products/categories/{id}：存在しない→404")
        void getCategory_notFound() throws Exception {
            var unknown = "00000000-0000-0000-0000-000000000000";
            given(usecase.getCategoryById(unknown))
                .willThrow(new NotFoundException("カテゴリが存在しません。"));

            mockMvc.perform(get("/api/products/categories/{id}", unknown))
                   .andExpect(status().isNotFound());
        }
    }

     @Nested
    class CheckExists {

        @Test
        @DisplayName("GET /api/products/exists?name=万年筆：未存在→204")
        void exists_notExists_204() throws Exception {
            // 例外を投げなければ未存在扱い（204）
            willDoNothing().given(usecase).existsProduct("万年筆");

            mockMvc.perform(get("/api/products/exists").param("name", "万年筆"))
                   .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("GET /api/products/exists?name=水性ボールペン(青)：既存→409")
        void exists_exists_409() throws Exception {
            willThrow(new ExistsException("既に存在"))
                .given(usecase).existsProduct("水性ボールペン(青)");

            mockMvc.perform(get("/api/products/exists").param("name", "水性ボールペン(青)"))
                   .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("GET /api/products/exists?name= (空白) ：400")
        void exists_blank_400() throws Exception {
            // パラメータバリデーション（@NotBlank）→ ConstraintViolationException → 400
            mockMvc.perform(get("/api/products/exists").param("name", " "))
                   .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class RegisterProduct {

        @SuppressWarnings("null")
        @Test
        @DisplayName("POST /api/products：201 & Location & ボディ")
        void register_created() throws Exception {
            // 入力
            var requestJson = """
                {
                  "name": "万年筆",
                  "price": 1200,
                  "categoryId": "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4",
                  "stockQuantity": 10
                }
                """;

            // Usecaseが返す新規作成後 DTO
            var created = new ProductDTO(
                "83fbc81d-2498-4da6-b8c2-54878d3b67ff",
                "万年筆",
                1200,
                new CategoryDTO("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", "文房具"),
                new StockDTO("s-1", 10)
            );

            // exists チェックは何も起きない（未存在）
            willDoNothing().given(usecase).existsProduct("万年筆");
            // addProduct は created を返す
            given(usecase.addProduct(any(ProductDTO.class))).willReturn(created);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                   .andExpect(status().isCreated())
                   .andExpect(header().string("Location",
                       "/api/products/" + created.getId()))
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.id").value(created.getId()))
                   .andExpect(jsonPath("$.name").value("万年筆"))
                   .andExpect(jsonPath("$.price").value(1200))
                   .andExpect(jsonPath("$.category.id").value("2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4"))
                   .andExpect(jsonPath("$.stock.quantity").value(10));

            then(usecase).should().existsProduct("万年筆");
            then(usecase).should().addProduct(any(ProductDTO.class));
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("POST /api/products：入力不正(@Valid) → 400")
        void register_badRequest() throws Exception {
            // name が空、price/stockQuantity が下限未満 等
            var badJson = """
                {
                  "name": " ",
                  "price": 10,
                  "categoryId": "",
                  "stockQuantity": -1
                }
                """;

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badJson))
                   .andExpect(status().isBadRequest());
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("POST /api/products：既存名 → 409")
        void register_conflict() throws Exception {
            var requestJson = """
                {
                  "name": "万年筆",
                  "price": 1200,
                  "categoryId": "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4",
                  "stockQuantity": 10
                }
                """;

            willThrow(new ExistsException("既に存在"))
                .given(usecase).existsProduct("万年筆");

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                   .andExpect(status().isConflict());
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("POST /api/products：カテゴリ無し → 404")
        void register_categoryNotFound() throws Exception {
            var requestJson = """
                {
                  "name": "万年筆",
                  "price": 1200,
                  "categoryId": "00000000-0000-0000-0000-000000000000",
                  "stockQuantity": 10
                }
                """;

            // exists は OK だが addProduct 時にカテゴリ無し
            willDoNothing().given(usecase).existsProduct("万年筆");
            given(usecase.addProduct(any(ProductDTO.class)))
                .willThrow(new NotFoundException("カテゴリが存在しません"));

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                   .andExpect(status().isNotFound());
        }
    }
}

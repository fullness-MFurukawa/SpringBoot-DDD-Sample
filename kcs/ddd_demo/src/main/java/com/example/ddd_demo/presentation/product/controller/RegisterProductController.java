package com.example.ddd_demo.presentation.product.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ddd_demo.application.dto.CategoryDTO;
import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.product.usecase.RegisterProductUsecase;
import com.example.ddd_demo.presentation.product.schema.ProductCreateSchema;
import com.example.ddd_demo.presentation.product.schema.ProductCreateSchemaMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * 【プレゼンテーション層：RegisterProductController】
 * ============================================================================
 * 🎯 役割
 * - ユースケース「商品を登録する」を実現するエンドポイント群を提供する。
 * - クライアントからの HTTP リクエストを受け取り、アプリケーション層の Usecase に委譲する。
 * - 入出力データの整形（Schema ⇔ DTO）および入力値の基本的な検証を担う。
 *
 * 🧩 設計方針
 * - Controller は「変換と委譲」に徹し、ビジネスロジックは一切持たない。
 * - Usecase 層（RegisterProductUsecase）がトランザクション境界を持つため、
 *   Controller では readOnly な操作に留める。
 * - ドメイン内部構造（Entity や ValueObject）を外部に晒さず、
 *   DTO / Schema で API 境界を明確に保つ。
 *
 * 📦 主なエンドポイント
 * - GET  /api/products/categories          : カテゴリ一覧取得
 * - GET  /api/products/categories/{id}     : カテゴリ詳細取得
 * - GET  /api/products/exists?name=XXX     : 商品名の存在チェック
 * - POST /api/products                     : 商品登録
 *
 * 💬 実装概要
 * - Request: ProductCreateSchema（入力スキーマ）
 * - Mapper : ProductCreateSchemaMapper により Schema → ProductDTO 変換
 * - Usecase: RegisterProductUsecase を通じてドメイン操作を実行
 * - Response: ProductDTO を JSON 形式で返却
 *
 * 🛡️ 例外ハンドリング
 * - 存在確認失敗：NotFoundException → 404
 * - 重複登録：ExistsException → 409
 * - 入力不備：InvalidInputException → 400
 * これらは ApiExceptionHandler にて統一処理される。
 *
 * ============================================================================
 */
@Tag(name = "RegisterProducts", description = "商品登録")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class RegisterProductController {
    /**
     * ユースケース:[商品を登録する]を実現するインターフェイス
     */
    private final RegisterProductUsecase usecase;

    private final ProductCreateSchemaMapper mapper;
   
    /**
     * 商品カテゴリ一覧を提供する
     * @return
     */
    @Operation(summary = "カテゴリ一覧取得",
        description = "登録時のプルダウンなどに使用するカテゴリ一覧を返します。")
    @ApiResponse(responseCode = "200", description = "取得成功",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CategoryDTO.class)))
    @GetMapping("/categories")
    public List<CategoryDTO> getCategories() {
        return usecase.getCategories();
    }

    /**
     * 指定された商品カテゴリIdの商品カテゴリを提供する
     * @param categoryId 商品カテゴリId
     * @return
     */
    @Operation(summary = "商品カテゴリ取得", 
        description = "カテゴリId(UUID)を指定してカテゴリ情報を取得します。")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取得成功"),
        @ApiResponse(responseCode = "404", description = "該当カテゴリが存在しない")
    })
    @GetMapping("/categories/{id}")
    public CategoryDTO getCategoryById(
        @Parameter(description = "商品カテゴリId(UUID)", example = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", required = true)
        @PathVariable("id") String categoryId
    ){
        return usecase.getCategoryById(categoryId);
    }

    /**
     * 指定された商品名の有無を調べた結果を提供する
     * @param name 商品名
     * @return
     */
    @Operation(summary = "商品名の存在チェック", 
        description = "指定した商品名が既に存在するか判定します。存在する場合は409を返します。")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "存在しない(登録可能)"),
        @ApiResponse(responseCode = "409", description = "既に存在する(登録不可)"),
        @ApiResponse(responseCode = "400", description = "入力不正")
    })
    @GetMapping("/exists")
    public ResponseEntity<Void> checkExists(
        @Parameter(description = "商品名", example = "万年筆", required = true)
        @RequestParam("name") @NotBlank(message = "商品名は必須です") String name
    ){
        usecase.existsProduct(name); 
        return ResponseEntity.noContent().build();
    }

    /**
     * 商品を登録する
     * @param req 登録リクエストDTO
     * @return
     */
    @SuppressWarnings("null")
    @Operation(
        summary = "商品登録",
        description = "商品を新規登録します。成功時は201Createdを返します。",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductCreateSchema.class)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "登録成功",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "入力不正"),
        @ApiResponse(responseCode = "404", description = "カテゴリが存在しない"),
        @ApiResponse(responseCode = "409", description = "同名商品が既に存在する"),
        @ApiResponse(responseCode = "500", description = "サーバ内部エラー")
    })
    @PostMapping(consumes="application/json", produces="application/json")
    public ResponseEntity<ProductDTO> register(@Valid @RequestBody ProductCreateSchema req) {
        // ProductCreateSchemaからProductDTOに変換する
        var dto = mapper.toDto(req);

        // 事前チェック:同一商品名の有無
        usecase.existsProduct(req.name());
        // 商品の登録
        var newProduct = usecase.addProduct(dto);
        var location = URI.create( "/api/products/" + newProduct.getId());
        return ResponseEntity.created(location).body(newProduct);
    }
}

package com.example.ddd_demo.presentation.product.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ddd_demo.application.dto.ProductDTO;
import com.example.ddd_demo.application.product.usecase.SearchProductByNameUsecase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * 【プレゼンテーション層：SearchProductByNameController】
 * ============================================================================
 * 🎯 役割
 * - ユースケース「商品名で検索する」を実現するエンドポイントを提供する。
 * - クライアントから指定された商品名を受け取り、アプリケーション層の Usecase に委譲する。
 * - HTTP レイヤにおけるリクエスト検証（@NotBlank）および
 *   例外（NotFound / InvalidInput）のマッピングを担う。
 *
 * 🧩 設計方針
 * - Controller 自体はビジネスロジックを一切持たない「薄い層」。
 * - トランザクション境界は Usecase 側（SearchProductByNameUsecase）に存在する。
 * - ProductDTO を返すことで、ドメイン内部構造（Entity / ValueObject）を完全に秘匿する。
 *
 * 📦 エンドポイント
 * - GET /api/products/search?name=XXX
 *   → 商品名を指定して商品情報を検索。
 *
 * 📄 入出力
 * - RequestParam: name（必須・空白不可）
 * - Response: ProductDTO（JSON形式）
 *
 * 🛡️ 例外ハンドリング
 * - NotFoundException → 404 Not Found
 * - InvalidInputException → 400 Bad Request
 *   これらは ApiExceptionHandler にて共通処理される。
 *
 * ============================================================================
 */
@Tag(name = "SearchProducts", description = "商品検索(名前で検索)")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class SearchProductByNameController {
    /**
     * ユースケース:[商品を名前で検索する]を実現するインターフェイス
     */
    private final SearchProductByNameUsecase usecase;

    /**
     * 商品名を指定して商品情報を取得する
     * 例: GET /api/products/search?name=蛍光ペン(赤)
     * @param name 商品名（必須・空白のみ不可）
     * @return ProductDTO
     */
    @Operation(
        summary = "商品名で検索",
        description = "商品名を指定して商品情報(ProductDTO)を取得します。"
    )
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "該当商品が存在しない場合")
    @ApiResponse(responseCode = "400", description = "入力パラメータが不正な場合")
    @GetMapping("/search")
    public ProductDTO searchByName(
        @Parameter(description = "商品名(必須・空白のみ不可)", required = true, example = "蛍光ペン(赤)")
        @RequestParam("name")  @NotBlank(message = "商品名は必須です")  String name) {
        return usecase.search(name);
    }
}

package com.example.ddd_demo.presentation.product.schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * 📦 商品登録用リクエスト受信用スキーマ（入力DTO）
 *
 * <p>このクラスは <b>プレゼンテーション層</b> で使用される入力専用の
 * データ転送オブジェクト（DTO）です。</p>
 *
 * <p>主な目的：</p>
 * <ul>
 *   <li>APIクライアント（フロントエンド）から送信されるJSONリクエストを受け取る</li>
 *   <li><b>Jakarta Bean Validation</b> アノテーションにより、入力値の妥当性を事前検証する</li>
 *   <li>ドメイン層の知識（値オブジェクトなど）を露出せずに、
 *       外部とのデータ受け渡し境界を明確にする</li>
 * </ul>
 *
 * <p>このスキーマは、Controller層で {@code @Valid} により自動検証され、
 * バリデーションエラーは {@link com.example.ddd_demo.presentation.exception.ApiExceptionHandler}
 * によってハンドリングされます。</p>
 *
 * <p>なお、受け取ったデータは {@link com.example.ddd_demo.presentation.product.schema.ProductCreateSchemaMapper}
 * によってアプリケーション層の {@code ProductDTO} に変換されます。</p>
 */
@Schema(name = "ProductCreateSchema", description = "商品登録リクエスト")
public record ProductCreateSchema(
    @Schema(description = "商品名", example = "筆ペン", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名は必須です")
    String name,

    @Schema(description = "商品単価（円）: 50〜10000", example = "300", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "単価は必須です")
    @Min(value = 50, message = "単価は50以上で指定してください")
    @Max(value = 10000, message = "単価は10000以下で指定してください")
    Integer price,

    @Schema(description = "商品カテゴリのUUID", example = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品カテゴリIdは必須です")
    String categoryId,

    @Schema(description = "初期在庫数: 0〜100", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "在庫数は必須です")
    @Min(value = 0, message = "在庫数は0以上で指定してください")
    @Max(value = 100, message = "在庫数は100以下で指定してください")
    Integer stockQuantity
){}


package com.example.ddd_demo.domain.mapper;

/**
 * DTOや外部データ構造からドメインエンティティを再構築するためのAdapterインターフェイス。
 *
 * <p>このインターフェイスはGoFの「Adapterパターン」をDDDに応用したものであり、
 * ドメイン層を外部依存（DTO, jOOQ Record, APIレスポンスなど）から隔離する
 * 「腐敗防止層（Anti-Corruption Layer, ACL）」としての役割を持ちます。
 * <p>
 * 外部で定義されたデータ構造をそのままドメインに持ち込まず、
 * <b>「ドメインモデルの語彙に変換して再構築する」</b>ことを目的としています。
 *
 * <p><b>利用例:</b>
 * <pre>{@code
 * Category entity = mapper.toDomain(record); // jOOQ Record → Domain Entity
 * }</pre>
 *
 * @param <DTO>    外部データ型（例：DTO, Record, Responseなど）
 * @param <DOMAIN> ドメインエンティティ型
 */
public interface ToDomainMapper<DTO , DOMAIN> {
    /**
     * 外部データ構造（DTO, Recordなど）からドメインエンティティを再構築します。
     * <p>変換過程では、必須項目の検証や値オブジェクトの生成などを通じて、
     * ドメインモデルの一貫性を保証します。
     *
     * @param input 変換する外部データ（DTO, Recordなど）
     * @return 検証済みのドメインエンティティ
     */
    DOMAIN toDomain(DTO input);
}

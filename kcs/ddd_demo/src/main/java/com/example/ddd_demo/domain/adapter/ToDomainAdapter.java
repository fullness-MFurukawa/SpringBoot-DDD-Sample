package com.example.ddd_demo.domain.adapter;
/**
 * 任意のDTOからドメインEntityを再構築するAdapterインターフェイス
 * <p>⚠️ MapStructを導入したため、このインターフェイスは使用しません。</p>
 * <p>今後は {@code @Mapper} を付与したMapStructインターフェイスを利用してください。</p>
 */
@Deprecated(since = "2025-10-26", forRemoval = true)
public interface ToDomainAdapter<DTO , DOMAIN> {
    /**
     * 任意のDTOからドメインエンティティを再構築する
     * @param input 変換するDTOなど
     * @return 変換結果のドメインエンティティ
     */
    DOMAIN toDomain(DTO input);
}

package com.example.ddd_demo.domain.adapter;
/**
 * 任意のDTOからドメインEntityを再構築するAdapterインターフェイス
 */
public interface ToDomainAdapter<DTO , DOMAIN> {
    /**
     * 任意のDTOからドメインエンティティを再構築する
     * @param input 変換するDTOなど
     * @return 変換結果のドメインエンティティ
     */
    DOMAIN toDomain(DTO input);
}

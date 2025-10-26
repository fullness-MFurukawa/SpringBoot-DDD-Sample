package com.example.ddd_demo.domain.adapter;

/**
 * 任意のドメインエンティティと任意のDTOなどを相互変換するAdapterインターフェイス
 * <p>⚠️ MapStructを導入したため、このインターフェイスは使用しません。</p>
 * <p>今後は {@code @Mapper} を付与したMapStructインターフェイスを利用してください。</p>
 */
@Deprecated(since = "2025-10-26", forRemoval = true)
public interface DomainBiAdapter<DTO , DOMAIN> {
    /**
     * 任意のDTOなどを任意のドメインエンティティに再構築する
     * @param input 任意のDTO
     * @return
     */
    DOMAIN toDomain(DTO input); 
    /**
     * 任意のドメインエンティティを任意のDTOに変換する
     * @param domain 任意のドメインエンティティ
     * @return 任意のDTO
     */
    DTO fromDomain(DOMAIN domain);
}

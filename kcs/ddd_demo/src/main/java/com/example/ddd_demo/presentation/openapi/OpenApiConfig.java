package com.example.ddd_demo.presentation.openapi;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "商品管理API",
        version = "v1.0",
        description = "DDD構成のサンプルアプリケーションAPIドキュメント"
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "ローカル環境")
    }
)
public class OpenApiConfig {

}

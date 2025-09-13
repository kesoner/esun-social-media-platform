package com.esun.socialmedia.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger API 文件配置
 * 
 * 配置 OpenAPI 3.0 規範的 API 文件
 * 
 * @author 開發團隊
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                    new Server()
                        .url("http://localhost:" + serverPort)
                        .description("開發環境"),
                    new Server()
                        .url("https://api.socialmedia.esun.com")
                        .description("正式環境")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                    .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("玉山銀行社群媒體平台 API")
                .description("""
                    ## 社群媒體平台後端 API 文件
                    
                    這是一個基於 Spring Boot 開發的社群媒體平台後端服務，提供以下主要功能：
                    
                    ### 🔐 認證功能
                    - 使用者註冊與登入
                    - JWT Token 認證
                    - 個人資料管理
                    
                    ### 📝 發文功能
                    - 建立、編輯、刪除發文
                    - 發文列表查詢與搜尋
                    - 熱門發文推薦
                    
                    ### 💬 留言功能
                    - 新增、刪除留言
                    - 留言列表查詢
                    - 留言統計
                    
                    ### 🔒 安全性
                    - BCrypt 密碼加密
                    - JWT Token 驗證
                    - CORS 跨域支援
                    - SQL Injection 防護
                    
                    ### 📊 資料庫
                    - SQLite 輕量級資料庫
                    - JPA/Hibernate ORM
                    - 自動建表與資料初始化
                    
                    ---
                    
                    **使用說明：**
                    1. 先呼叫註冊或登入 API 獲取 JWT Token
                    2. 在後續請求的 Authorization Header 中加入 `Bearer {token}`
                    3. 所有需要認證的 API 都需要提供有效的 JWT Token
                    
                    **技術棧：**
                    - Spring Boot 3.2.0
                    - Spring Security 6.x
                    - Spring Data JPA
                    - SQLite Database
                    - JWT Authentication
                    - OpenAPI 3.0 (Swagger)
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("開發團隊")
                    .email("dev-team@esun.com")
                    .url("https://www.esunbank.com.tw"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("請在此輸入 JWT Token（不需要加 'Bearer ' 前綴）");
    }
}

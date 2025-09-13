package com.esun.socialmedia.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 資料庫配置類別
 *
 * 配置 JPA、Repository 相關設定
 *
 * @author 開發團隊
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.esun.socialmedia.repository")
@EntityScan(basePackages = "com.esun.socialmedia.entity")
@EnableTransactionManagement
public class DatabaseConfig {
    // H2 資料庫不需要特殊初始化配置
}

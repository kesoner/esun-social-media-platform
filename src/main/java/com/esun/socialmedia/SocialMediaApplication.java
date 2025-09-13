package com.esun.socialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 社群媒體平台主應用程式
 * 
 * 玉山銀行後端工程師實作題
 * 
 * @author 開發團隊
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class SocialMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaApplication.class, args);
    }
}

package com.esun.socialmedia.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登入請求 DTO
 * 
 * @author 開發團隊
 */
public class LoginRequest {

    @JsonProperty("usernameOrEmail")
    @NotBlank(message = "使用者名稱或電子郵件不能為空")
    @Size(max = 100, message = "使用者名稱或電子郵件長度不能超過 100 字元")
    private String usernameOrEmail;

    @JsonProperty("password")
    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, max = 128, message = "密碼長度必須在 8-128 字元之間")
    private String password;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("LoginRequest{usernameOrEmail='%s'}", usernameOrEmail);
    }
}

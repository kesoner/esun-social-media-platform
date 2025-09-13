package com.esun.socialmedia.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 註冊請求 DTO
 * 
 * @author 開發團隊
 */
public class RegisterRequest {

    @JsonProperty("username")
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 3, max = 50, message = "使用者名稱長度必須在 3-50 字元之間")
    private String username;

    @JsonProperty("email")
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 100, message = "電子郵件長度不能超過 100 字元")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, max = 128, message = "密碼長度必須在 8-128 字元之間")
    private String password;

    @JsonProperty("confirmPassword")
    @NotBlank(message = "確認密碼不能為空")
    private String confirmPassword;

    @JsonProperty("biography")
    @Size(max = 500, message = "個人簡介長度不能超過 500 字元")
    private String biography;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public RegisterRequest(String username, String email, String password, String biography) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.biography = biography;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return String.format("RegisterRequest{username='%s', email='%s'}", username, email);
    }
}

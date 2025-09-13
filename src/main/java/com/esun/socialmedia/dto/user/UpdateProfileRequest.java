package com.esun.socialmedia.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 更新個人資料請求 DTO
 * 
 * @author 開發團隊
 */
public class UpdateProfileRequest {

    @Size(min = 3, max = 50, message = "使用者名稱長度必須在 3-50 字元之間")
    private String username;

    @Email(message = "電子郵件格式不正確")
    @Size(max = 100, message = "電子郵件長度不能超過 100 字元")
    private String email;

    @Size(max = 500, message = "個人簡介長度不能超過 500 字元")
    private String biography;

    private String coverImage;

    // Constructors
    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String username, String email, String biography) {
        this.username = username;
        this.email = email;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    public String toString() {
        return String.format("UpdateProfileRequest{username='%s', email='%s'}", username, email);
    }
}

package com.esun.socialmedia.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新發文請求 DTO
 * 
 * @author 開發團隊
 */
public class UpdatePostRequest {

    @NotBlank(message = "文章內容不能為空")
    @Size(max = 5000, message = "文章內容長度不能超過 5000 字元")
    private String content;

    private String image;

    // Constructors
    public UpdatePostRequest() {
    }

    public UpdatePostRequest(String content) {
        this.content = content;
    }

    public UpdatePostRequest(String content, String image) {
        this.content = content;
        this.image = image;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return String.format("UpdatePostRequest{content='%s', hasImage=%s}", 
            content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content,
            image != null && !image.isEmpty());
    }
}

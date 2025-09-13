package com.esun.socialmedia.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 建立留言請求 DTO
 * 
 * @author 開發團隊
 */
public class CreateCommentRequest {

    @NotBlank(message = "留言內容不能為空")
    @Size(max = 1000, message = "留言內容長度不能超過 1000 字元")
    private String content;

    // Constructors
    public CreateCommentRequest() {
    }

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("CreateCommentRequest{content='%s'}", 
            content != null && content.length() > 30 ? content.substring(0, 30) + "..." : content);
    }
}

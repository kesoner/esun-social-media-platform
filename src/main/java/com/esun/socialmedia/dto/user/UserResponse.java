package com.esun.socialmedia.dto.user;

import com.esun.socialmedia.entity.User;

import java.time.LocalDateTime;

/**
 * 使用者回應 DTO
 * 
 * @author 開發團隊
 */
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String coverImage;
    private String biography;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long postCount;
    private Long commentCount;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.coverImage = user.getCoverImage();
        this.biography = user.getBiography();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    // Static factory method
    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    public static UserResponse fromWithCounts(User user, Long postCount, Long commentCount) {
        UserResponse response = new UserResponse(user);
        response.setPostCount(postCount);
        response.setCommentCount(commentCount);
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return String.format("UserResponse{id=%d, username='%s', email='%s', postCount=%d, commentCount=%d}", 
            id, username, email, postCount, commentCount);
    }
}

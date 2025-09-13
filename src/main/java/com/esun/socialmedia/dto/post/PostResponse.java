package com.esun.socialmedia.dto.post;

import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.entity.Post;

import java.time.LocalDateTime;

/**
 * 發文回應 DTO
 * 
 * @author 開發團隊
 */
public class PostResponse {

    private Long id;
    private String content;
    private String image;
    private UserResponse author;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PostResponse() {
    }

    public PostResponse(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.image = post.getImage();
        this.author = UserResponse.from(post.getAuthor());
        this.commentCount = (long) post.getComments().size();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    // Static factory methods
    public static PostResponse from(Post post) {
        return new PostResponse(post);
    }

    public static PostResponse fromWithCommentCount(Post post, Long commentCount) {
        PostResponse response = new PostResponse(post);
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

    public UserResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserResponse author) {
        this.author = author;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
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

    @Override
    public String toString() {
        return String.format("PostResponse{id=%d, author='%s', commentCount=%d, createdAt=%s}", 
            id, 
            author != null ? author.getUsername() : "null",
            commentCount,
            createdAt);
    }
}

package com.esun.socialmedia.dto.comment;

import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.entity.Comment;

import java.time.LocalDateTime;

/**
 * 留言回應 DTO
 * 
 * @author 開發團隊
 */
public class CommentResponse {

    private Long id;
    private String content;
    private UserResponse author;
    private Long postId;
    private LocalDateTime createdAt;

    // Constructors
    public CommentResponse() {
    }

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = UserResponse.from(comment.getAuthor());
        this.postId = comment.getPost().getId();
        this.createdAt = comment.getCreatedAt();
    }

    // Static factory method
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment);
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

    public UserResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserResponse author) {
        this.author = author;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("CommentResponse{id=%d, author='%s', postId=%d, createdAt=%s}", 
            id, 
            author != null ? author.getUsername() : "null",
            postId,
            createdAt);
    }
}

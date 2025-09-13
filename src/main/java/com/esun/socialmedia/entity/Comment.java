package com.esun.socialmedia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 留言實體類別
 * 
 * 對應資料庫 comments 表格
 * 
 * @author 開發團隊
 */
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @NotBlank(message = "留言內容不能為空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 多對一關聯：留言作者
    @NotNull(message = "留言作者不能為空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_user_id"))
    private User author;

    // 多對一關聯：所屬文章
    @NotNull(message = "所屬文章不能為空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_post_id"))
    private Post post;

    // Constructors
    public Comment() {
    }

    public Comment(String content, User author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    // Helper methods
    public boolean isAuthor(User user) {
        return author != null && author.equals(user);
    }

    @Override
    public String toString() {
        return String.format("Comment{id=%d, content='%s', author='%s', postId=%d, createdAt=%s}", 
            getId(), 
            content.length() > 30 ? content.substring(0, 30) + "..." : content,
            author != null ? author.getUsername() : "null",
            post != null ? post.getId() : null,
            getCreatedAt());
    }
}

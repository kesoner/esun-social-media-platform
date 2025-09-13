package com.esun.socialmedia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 發文實體類別
 * 
 * 對應資料庫 posts 表格
 * 
 * @author 開發團隊
 */
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @NotBlank(message = "文章內容不能為空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image")
    private String image;

    // 多對一關聯：文章作者
    @NotNull(message = "文章作者不能為空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_posts_user_id"))
    private User author;

    // 一對多關聯：文章的留言
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Comment> comments = new ArrayList<>();

    // Constructors
    public Post() {
    }

    public Post(String content, User author) {
        this.content = content;
        this.author = author;
    }

    public Post(String content, String image, User author) {
        this.content = content;
        this.image = image;
        this.author = author;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Helper methods
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public int getCommentCount() {
        return comments.size();
    }

    public boolean isAuthor(User user) {
        return author != null && author.equals(user);
    }

    @Override
    public String toString() {
        return String.format("Post{id=%d, content='%s', author='%s', commentCount=%d, createdAt=%s}", 
            getId(), 
            content.length() > 50 ? content.substring(0, 50) + "..." : content,
            author != null ? author.getUsername() : "null",
            getCommentCount(),
            getCreatedAt());
    }
}

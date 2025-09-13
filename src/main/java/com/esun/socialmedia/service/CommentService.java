package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.comment.CommentResponse;
import com.esun.socialmedia.dto.comment.CreateCommentRequest;
import com.esun.socialmedia.entity.Comment;
import com.esun.socialmedia.entity.Post;
import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.repository.CommentRepository;
import com.esun.socialmedia.repository.PostRepository;
import com.esun.socialmedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 留言服務類別
 * 
 * 提供留言相關的業務邏輯處理
 * 
 * @author 開發團隊
 */
@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                         PostRepository postRepository,
                         UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * 建立新留言
     * 
     * @param postId 發文 ID
     * @param request 建立留言請求
     * @param authorId 作者 ID
     * @return 建立的留言
     */
    public CommentResponse createComment(Long postId, CreateCommentRequest request, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("找不到發文: " + postId));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + authorId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    /**
     * 根據 ID 獲取留言
     * 
     * @param commentId 留言 ID
     * @return 留言資訊
     */
    @Transactional(readOnly = true)
    public Optional<CommentResponse> getCommentById(Long commentId) {
        return commentRepository.findByIdWithAuthorAndPost(commentId)
                .map(CommentResponse::from);
    }

    /**
     * 根據發文 ID 獲取留言列表
     * 
     * @param postId 發文 ID
     * @return 留言列表
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 根據發文 ID 獲取留言列表（分頁）
     * 
     * @param postId 發文 ID
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 留言分頁列表
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPostId(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
        
        return comments.map(CommentResponse::from);
    }

    /**
     * 根據作者 ID 獲取留言列表
     * 
     * @param authorId 作者 ID
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 留言分頁列表
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
        
        return comments.map(CommentResponse::from);
    }

    /**
     * 刪除留言
     * 
     * @param commentId 留言 ID
     * @param currentUserId 當前使用者 ID
     */
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findByIdWithAuthorAndPost(commentId)
                .orElseThrow(() -> new IllegalArgumentException("找不到留言: " + commentId));

        // 檢查權限：留言作者或發文作者可以刪除留言
        if (!comment.getAuthor().getId().equals(currentUserId) && 
            !comment.getPost().getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("沒有權限刪除此留言");
        }

        commentRepository.delete(comment);
    }

    /**
     * 統計發文的留言數量
     * 
     * @param postId 發文 ID
     * @return 留言數量
     */
    @Transactional(readOnly = true)
    public long countCommentsByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    /**
     * 統計使用者的留言數量
     * 
     * @param authorId 作者 ID
     * @return 留言數量
     */
    @Transactional(readOnly = true)
    public long countCommentsByAuthor(Long authorId) {
        return commentRepository.countByAuthorId(authorId);
    }

    /**
     * 搜尋留言
     * 
     * @param keyword 關鍵字
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 符合條件的留言分頁列表
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> searchComments(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByContentContaining(keyword, pageable);
        
        return comments.map(CommentResponse::from);
    }

    /**
     * 獲取最新留言
     * 
     * @param limit 數量限制
     * @return 最新留言列表
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getLatestComments(int limit) {
        List<Comment> comments = commentRepository.findLatestComments(limit);
        
        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 獲取指定時間範圍內的留言
     * 
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 留言分頁列表
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime, pageable);
        
        return comments.map(CommentResponse::from);
    }

    /**
     * 檢查使用者是否為留言作者
     * 
     * @param commentId 留言 ID
     * @param userId 使用者 ID
     * @return 是否為作者
     */
    @Transactional(readOnly = true)
    public boolean isCommentAuthor(Long commentId, Long userId) {
        return commentRepository.isAuthor(commentId, userId);
    }

    /**
     * 檢查使用者是否可以刪除留言（留言作者或發文作者）
     * 
     * @param commentId 留言 ID
     * @param userId 使用者 ID
     * @return 是否可以刪除
     */
    @Transactional(readOnly = true)
    public boolean canDeleteComment(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findByIdWithAuthorAndPost(commentId);
        if (commentOpt.isEmpty()) {
            return false;
        }

        Comment comment = commentOpt.get();
        return comment.getAuthor().getId().equals(userId) || 
               comment.getPost().getAuthor().getId().equals(userId);
    }
}

package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.post.CreatePostRequest;
import com.esun.socialmedia.dto.post.PostResponse;
import com.esun.socialmedia.dto.post.UpdatePostRequest;
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
 * 發文服務類別
 * 
 * 提供發文相關的業務邏輯處理
 * 
 * @author 開發團隊
 */
@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                      UserRepository userRepository,
                      CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 建立新發文
     * 
     * @param request 建立發文請求
     * @param authorId 作者 ID
     * @return 建立的發文
     */
    public PostResponse createPost(CreatePostRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + authorId));

        Post post = new Post();
        post.setContent(request.getContent());
        post.setImage(request.getImage());
        post.setAuthor(author);

        Post savedPost = postRepository.save(post);
        return PostResponse.fromWithCommentCount(savedPost, 0L);
    }

    /**
     * 根據 ID 獲取發文
     * 
     * @param postId 發文 ID
     * @return 發文資訊
     */
    @Transactional(readOnly = true)
    public Optional<PostResponse> getPostById(Long postId) {
        return postRepository.findByIdWithAuthor(postId)
                .map(post -> {
                    Long commentCount = commentRepository.countByPostId(postId);
                    return PostResponse.fromWithCommentCount(post, commentCount);
                });
    }

    /**
     * 獲取所有發文（分頁）
     * 
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 發文分頁列表
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        return posts.map(post -> {
            Long commentCount = commentRepository.countByPostId(post.getId());
            return PostResponse.fromWithCommentCount(post, commentCount);
        });
    }

    /**
     * 根據作者獲取發文（分頁）
     * 
     * @param authorId 作者 ID
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 發文分頁列表
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
        
        return posts.map(post -> {
            Long commentCount = commentRepository.countByPostId(post.getId());
            return PostResponse.fromWithCommentCount(post, commentCount);
        });
    }

    /**
     * 搜尋發文
     * 
     * @param keyword 關鍵字
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 符合條件的發文分頁列表
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByContentContaining(keyword, pageable);
        
        return posts.map(post -> {
            Long commentCount = commentRepository.countByPostId(post.getId());
            return PostResponse.fromWithCommentCount(post, commentCount);
        });
    }

    /**
     * 更新發文
     * 
     * @param postId 發文 ID
     * @param request 更新請求
     * @param currentUserId 當前使用者 ID
     * @return 更新後的發文
     */
    public PostResponse updatePost(Long postId, UpdatePostRequest request, Long currentUserId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new IllegalArgumentException("找不到發文: " + postId));

        // 檢查權限
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("沒有權限編輯此發文");
        }

        post.setContent(request.getContent());
        post.setImage(request.getImage());

        Post updatedPost = postRepository.save(post);
        Long commentCount = commentRepository.countByPostId(postId);
        
        return PostResponse.fromWithCommentCount(updatedPost, commentCount);
    }

    /**
     * 刪除發文
     * 
     * @param postId 發文 ID
     * @param currentUserId 當前使用者 ID
     */
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new IllegalArgumentException("找不到發文: " + postId));

        // 檢查權限
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("沒有權限刪除此發文");
        }

        postRepository.delete(post);
    }

    /**
     * 獲取熱門發文
     * 
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 熱門發文分頁列表
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPopularPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findPopularPosts(pageable);
        
        return posts.map(post -> {
            Long commentCount = commentRepository.countByPostId(post.getId());
            return PostResponse.fromWithCommentCount(post, commentCount);
        });
    }

    /**
     * 獲取最新發文
     * 
     * @param limit 數量限制
     * @return 最新發文列表
     */
    @Transactional(readOnly = true)
    public List<PostResponse> getLatestPosts(int limit) {
        List<Post> posts = postRepository.findLatestPosts(limit);
        
        return posts.stream()
                .map(post -> {
                    Long commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponse.fromWithCommentCount(post, commentCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 獲取指定時間範圍內的發文
     * 
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return 發文分頁列表
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime, pageable);
        
        return posts.map(post -> {
            Long commentCount = commentRepository.countByPostId(post.getId());
            return PostResponse.fromWithCommentCount(post, commentCount);
        });
    }

    /**
     * 檢查使用者是否為發文作者
     * 
     * @param postId 發文 ID
     * @param userId 使用者 ID
     * @return 是否為作者
     */
    @Transactional(readOnly = true)
    public boolean isPostAuthor(Long postId, Long userId) {
        return postRepository.isAuthor(postId, userId);
    }
}

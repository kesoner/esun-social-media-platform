package com.esun.socialmedia.repository;

import com.esun.socialmedia.entity.Post;
import com.esun.socialmedia.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 發文資料存取介面
 * 
 * 提供發文相關的資料庫操作方法
 * 
 * @author 開發團隊
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 查詢所有發文，按建立時間降序排列
     * 
     * @param pageable 分頁參數
     * @return 發文分頁列表
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 根據作者查詢發文
     * 
     * @param author 作者
     * @param pageable 分頁參數
     * @return 發文分頁列表
     */
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * 根據作者 ID 查詢發文
     * 
     * @param authorId 作者 ID
     * @param pageable 分頁參數
     * @return 發文分頁列表
     */
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * 查詢發文及其作者資訊
     * 
     * @param postId 發文 ID
     * @return 發文資訊（包含作者）
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :postId")
    Optional<Post> findByIdWithAuthor(@Param("postId") Long postId);

    /**
     * 查詢發文及其留言
     * 
     * @param postId 發文 ID
     * @return 發文資訊（包含留言）
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments c LEFT JOIN FETCH c.author WHERE p.id = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") Long postId);

    /**
     * 根據內容關鍵字搜尋發文
     * 
     * @param keyword 關鍵字
     * @param pageable 分頁參數
     * @return 符合條件的發文分頁列表
     */
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    Page<Post> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查詢指定時間範圍內的發文
     * 
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @param pageable 分頁參數
     * @return 發文分頁列表
     */
    Page<Post> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );

    /**
     * 統計使用者的發文數量
     * 
     * @param author 作者
     * @return 發文數量
     */
    long countByAuthor(User author);

    /**
     * 統計使用者的發文數量（根據 ID）
     * 
     * @param authorId 作者 ID
     * @return 發文數量
     */
    long countByAuthorId(Long authorId);

    /**
     * 查詢最新的發文
     * 
     * @param limit 數量限制
     * @return 最新發文列表
     */
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC LIMIT :limit")
    List<Post> findLatestPosts(@Param("limit") int limit);

    /**
     * 查詢熱門發文（根據留言數量）
     * 
     * @param pageable 分頁參數
     * @return 熱門發文分頁列表
     */
    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    /**
     * 檢查使用者是否為發文作者
     * 
     * @param postId 發文 ID
     * @param userId 使用者 ID
     * @return 是否為作者
     */
    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE p.id = :postId AND p.author.id = :userId")
    boolean isAuthor(@Param("postId") Long postId, @Param("userId") Long userId);
}

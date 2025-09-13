package com.esun.socialmedia.repository;

import com.esun.socialmedia.entity.Comment;
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
 * 留言資料存取介面
 * 
 * 提供留言相關的資料庫操作方法
 * 
 * @author 開發團隊
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根據發文查詢留言，按建立時間升序排列
     * 
     * @param post 發文
     * @return 留言列表
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * 根據發文 ID 查詢留言，按建立時間升序排列
     * 
     * @param postId 發文 ID
     * @return 留言列表
     */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    /**
     * 根據發文查詢留言（分頁）
     * 
     * @param post 發文
     * @param pageable 分頁參數
     * @return 留言分頁列表
     */
    Page<Comment> findByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    /**
     * 根據發文 ID 查詢留言（分頁）
     * 
     * @param postId 發文 ID
     * @param pageable 分頁參數
     * @return 留言分頁列表
     */
    Page<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    /**
     * 根據作者查詢留言
     * 
     * @param author 作者
     * @param pageable 分頁參數
     * @return 留言分頁列表
     */
    Page<Comment> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * 根據作者 ID 查詢留言
     * 
     * @param authorId 作者 ID
     * @param pageable 分頁參數
     * @return 留言分頁列表
     */
    Page<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * 查詢留言及其作者和發文資訊
     * 
     * @param commentId 留言 ID
     * @return 留言資訊（包含作者和發文）
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.post WHERE c.id = :commentId")
    Optional<Comment> findByIdWithAuthorAndPost(@Param("commentId") Long commentId);

    /**
     * 統計發文的留言數量
     * 
     * @param post 發文
     * @return 留言數量
     */
    long countByPost(Post post);

    /**
     * 統計發文的留言數量（根據 ID）
     * 
     * @param postId 發文 ID
     * @return 留言數量
     */
    long countByPostId(Long postId);

    /**
     * 統計使用者的留言數量
     * 
     * @param author 作者
     * @return 留言數量
     */
    long countByAuthor(User author);

    /**
     * 統計使用者的留言數量（根據 ID）
     * 
     * @param authorId 作者 ID
     * @return 留言數量
     */
    long countByAuthorId(Long authorId);

    /**
     * 查詢指定時間範圍內的留言
     * 
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @param pageable 分頁參數
     * @return 留言分頁列表
     */
    Page<Comment> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );

    /**
     * 根據內容關鍵字搜尋留言
     * 
     * @param keyword 關鍵字
     * @param pageable 分頁參數
     * @return 符合條件的留言分頁列表
     */
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    Page<Comment> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查詢最新的留言
     * 
     * @param limit 數量限制
     * @return 最新留言列表
     */
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC LIMIT :limit")
    List<Comment> findLatestComments(@Param("limit") int limit);

    /**
     * 檢查使用者是否為留言作者
     * 
     * @param commentId 留言 ID
     * @param userId 使用者 ID
     * @return 是否為作者
     */
    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.id = :commentId AND c.author.id = :userId")
    boolean isAuthor(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 刪除指定發文的所有留言
     * 
     * @param postId 發文 ID
     */
    void deleteByPostId(Long postId);
}

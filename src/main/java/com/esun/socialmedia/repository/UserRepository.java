package com.esun.socialmedia.repository;

import com.esun.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 使用者資料存取介面
 * 
 * 提供使用者相關的資料庫操作方法
 * 
 * @author 開發團隊
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根據使用者名稱查詢使用者
     * 
     * @param username 使用者名稱
     * @return 使用者資訊（如果存在）
     */
    Optional<User> findByUsername(String username);

    /**
     * 根據電子郵件查詢使用者
     * 
     * @param email 電子郵件
     * @return 使用者資訊（如果存在）
     */
    Optional<User> findByEmail(String email);

    /**
     * 根據使用者名稱或電子郵件查詢使用者
     * 
     * @param username 使用者名稱
     * @param email 電子郵件
     * @return 使用者資訊（如果存在）
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * 檢查使用者名稱是否已存在
     * 
     * @param username 使用者名稱
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 檢查電子郵件是否已存在
     * 
     * @param email 電子郵件
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 檢查使用者名稱是否已存在（排除指定 ID）
     * 
     * @param username 使用者名稱
     * @param userId 要排除的使用者 ID
     * @return 是否存在
     */
    boolean existsByUsernameAndIdNot(String username, Long userId);

    /**
     * 檢查電子郵件是否已存在（排除指定 ID）
     * 
     * @param email 電子郵件
     * @param userId 要排除的使用者 ID
     * @return 是否存在
     */
    boolean existsByEmailAndIdNot(String email, Long userId);

    /**
     * 查詢使用者及其發文數量
     * 
     * @param userId 使用者 ID
     * @return 使用者資訊（包含發文數量）
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.posts WHERE u.id = :userId")
    Optional<User> findByIdWithPosts(@Param("userId") Long userId);

    /**
     * 查詢使用者及其留言數量
     * 
     * @param userId 使用者 ID
     * @return 使用者資訊（包含留言數量）
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.comments WHERE u.id = :userId")
    Optional<User> findByIdWithComments(@Param("userId") Long userId);

    /**
     * 根據使用者名稱模糊查詢
     * 
     * @param username 使用者名稱關鍵字
     * @return 符合條件的使用者列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    java.util.List<User> findByUsernameContaining(@Param("username") String username);
}

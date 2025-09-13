package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.user.UpdateProfileRequest;
import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.repository.CommentRepository;
import com.esun.socialmedia.repository.PostRepository;
import com.esun.socialmedia.repository.UserRepository;
import com.esun.socialmedia.security.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 使用者服務類別
 * 
 * 提供使用者相關的業務邏輯處理
 * 
 * @author 開發團隊
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordService passwordService;

    @Autowired
    public UserService(UserRepository userRepository,
                      PostRepository postRepository,
                      CommentRepository commentRepository,
                      PasswordService passwordService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.passwordService = passwordService;
    }

    /**
     * 建立新使用者
     * 
     * @param username 使用者名稱
     * @param email 電子郵件
     * @param rawPassword 原始密碼
     * @param biography 個人簡介
     * @return 建立的使用者
     */
    public User createUser(String username, String email, String rawPassword, String biography) {
        // 檢查使用者名稱是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("使用者名稱已存在: " + username);
        }

        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("電子郵件已存在: " + email);
        }

        // 驗證密碼強度
        String passwordError = passwordService.validatePassword(rawPassword);
        if (passwordError != null) {
            throw new IllegalArgumentException(passwordError);
        }

        // 建立使用者
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordService.encodePassword(rawPassword));
        user.setBiography(biography);

        return userRepository.save(user);
    }

    /**
     * 根據 ID 查詢使用者
     * 
     * @param userId 使用者 ID
     * @return 使用者資訊
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * 根據使用者名稱查詢使用者
     * 
     * @param username 使用者名稱
     * @return 使用者資訊
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根據電子郵件查詢使用者
     * 
     * @param email 電子郵件
     * @return 使用者資訊
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 根據使用者名稱或電子郵件查詢使用者
     * 
     * @param usernameOrEmail 使用者名稱或電子郵件
     * @return 使用者資訊
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    /**
     * 獲取使用者詳細資訊（包含統計數據）
     * 
     * @param userId 使用者 ID
     * @return 使用者回應 DTO
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Long postCount = postRepository.countByAuthorId(userId);
                    Long commentCount = commentRepository.countByAuthorId(userId);
                    return UserResponse.fromWithCounts(user, postCount, commentCount);
                });
    }

    /**
     * 更新使用者個人資料
     * 
     * @param userId 使用者 ID
     * @param request 更新請求
     * @return 更新後的使用者資訊
     */
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + userId));

        // 更新使用者名稱
        if (StringUtils.hasText(request.getUsername()) && 
            !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
                throw new IllegalArgumentException("使用者名稱已存在: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // 更新電子郵件
        if (StringUtils.hasText(request.getEmail()) && 
            !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new IllegalArgumentException("電子郵件已存在: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // 更新個人簡介
        if (request.getBiography() != null) {
            user.setBiography(request.getBiography());
        }

        // 更新封面圖片
        if (request.getCoverImage() != null) {
            user.setCoverImage(request.getCoverImage());
        }

        User updatedUser = userRepository.save(user);
        
        Long postCount = postRepository.countByAuthorId(userId);
        Long commentCount = commentRepository.countByAuthorId(userId);
        
        return UserResponse.fromWithCounts(updatedUser, postCount, commentCount);
    }

    /**
     * 搜尋使用者
     * 
     * @param keyword 關鍵字
     * @return 符合條件的使用者列表
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String keyword) {
        List<User> users = userRepository.findByUsernameContaining(keyword);
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 檢查使用者名稱是否可用
     * 
     * @param username 使用者名稱
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * 檢查電子郵件是否可用
     * 
     * @param email 電子郵件
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * 驗證使用者密碼
     * 
     * @param user 使用者
     * @param rawPassword 原始密碼
     * @return 是否匹配
     */
    @Transactional(readOnly = true)
    public boolean validatePassword(User user, String rawPassword) {
        return passwordService.matches(rawPassword, user.getPasswordHash());
    }
}

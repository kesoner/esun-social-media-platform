package com.esun.socialmedia.security;

import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定義 UserDetailsService 實作
 * 
 * 從資料庫載入使用者資訊以供 Spring Security 使用
 * 
 * @author 開發團隊
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根據使用者名稱載入使用者詳細資訊
     * 
     * @param username 使用者名稱或電子郵件
     * @return UserDetails 實作
     * @throws UsernameNotFoundException 當使用者不存在時拋出
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    String.format("找不到使用者: %s", username)));

        return new UserPrincipal(user);
    }

    /**
     * 根據使用者 ID 載入使用者詳細資訊
     * 
     * @param userId 使用者 ID
     * @return UserDetails 實作
     * @throws UsernameNotFoundException 當使用者不存在時拋出
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                    String.format("找不到使用者 ID: %d", userId)));

        return new UserPrincipal(user);
    }

    /**
     * 檢查使用者是否存在
     * 
     * @param username 使用者名稱或電子郵件
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean userExists(String username) {
        return userRepository.findByUsernameOrEmail(username, username).isPresent();
    }

    /**
     * 檢查使用者名稱是否已被使用
     * 
     * @param username 使用者名稱
     * @return 是否已被使用
     */
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 檢查電子郵件是否已被使用
     * 
     * @param email 電子郵件
     * @return 是否已被使用
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}

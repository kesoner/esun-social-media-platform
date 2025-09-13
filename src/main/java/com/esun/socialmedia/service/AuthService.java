package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.auth.AuthResponse;
import com.esun.socialmedia.dto.auth.LoginRequest;
import com.esun.socialmedia.dto.auth.RegisterRequest;
import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.security.JwtUtil;
import com.esun.socialmedia.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認證服務類別
 * 
 * 提供使用者註冊、登入等認證相關的業務邏輯
 * 
 * @author 開發團隊
 */
@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    public AuthService(UserService userService,
                      AuthenticationManager authenticationManager,
                      JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 使用者註冊
     *
     * @param request 註冊請求
     * @return 認證回應
     */
    public AuthResponse register(RegisterRequest request) {
        // 驗證密碼確認
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("密碼與確認密碼不一致");
        }

        // 建立使用者
        User user = userService.createUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getBiography()
        );

        // 產生 JWT Token
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String accessToken = jwtUtil.generateToken(userPrincipal);
        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal);

        // 建立回應
        UserResponse userResponse = UserResponse.fromWithCounts(user, 0L, 0L);
        return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userResponse);
    }

    /**
     * 使用者登入
     * 
     * @param request 登入請求
     * @return 認證回應
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // 進行身份驗證
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsernameOrEmail(),
                    request.getPassword()
                )
            );

            // 獲取使用者資訊
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            // 產生 JWT Token
            String accessToken = jwtUtil.generateToken(userPrincipal);
            String refreshToken = jwtUtil.generateRefreshToken(userPrincipal);

            // 獲取使用者統計資料
            UserResponse userResponse = userService.getUserProfile(user.getId())
                    .orElse(UserResponse.from(user));

            return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userResponse);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("使用者名稱或密碼錯誤");
        }
    }

    /**
     * 刷新 Token
     * 
     * @param refreshToken 刷新 Token
     * @return 新的認證回應
     */
    public AuthResponse refreshToken(String refreshToken) {
        // 驗證 Refresh Token
        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("無效的 Refresh Token");
        }

        // 從 Token 中提取使用者名稱
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userService.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + username));

        // 產生新的 Token
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String newAccessToken = jwtUtil.generateToken(userPrincipal);
        String newRefreshToken = jwtUtil.generateRefreshToken(userPrincipal);

        // 獲取使用者統計資料
        UserResponse userResponse = userService.getUserProfile(user.getId())
                .orElse(UserResponse.from(user));

        return new AuthResponse(newAccessToken, newRefreshToken, jwtExpiration / 1000, userResponse);
    }

    /**
     * 驗證 Token
     * 
     * @param token JWT Token
     * @return 是否有效
     */
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        try {
            if (!jwtUtil.isTokenValid(token)) {
                return false;
            }

            String username = jwtUtil.extractUsername(token);
            return userService.findByUsernameOrEmail(username).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 從 Token 中獲取使用者資訊
     * 
     * @param token JWT Token
     * @return 使用者資訊
     */
    @Transactional(readOnly = true)
    public UserResponse getUserFromToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("無效的 Token");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userService.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + username));

        return userService.getUserProfile(user.getId())
                .orElse(UserResponse.from(user));
    }

    /**
     * 檢查使用者名稱是否可用
     * 
     * @param username 使用者名稱
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return userService.isUsernameAvailable(username);
    }

    /**
     * 檢查電子郵件是否可用
     * 
     * @param email 電子郵件
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return userService.isEmailAvailable(email);
    }
}

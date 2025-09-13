package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.auth.AuthResponse;
import com.esun.socialmedia.dto.auth.LoginRequest;
import com.esun.socialmedia.dto.auth.RegisterRequest;
import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.security.JwtUtil;
import com.esun.socialmedia.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService 測試類別
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setBiography("測試使用者");

        userPrincipal = new UserPrincipal(testUser);

        // 設置 JWT 過期時間
        ReflectionTestUtils.setField(authService, "jwtExpiration", 3600000L);
    }

    @Test
    void testRegister_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setBiography("新使用者");

        when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserPrincipal.class))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(UserPrincipal.class))).thenReturn("refresh-token");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
        verify(userService).createUser("newuser", "new@example.com", "password123", "新使用者");
    }

    @Test
    void testRegister_UserCreationFails() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("使用者名稱已存在"));

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("使用者名稱已存在");
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtil.generateToken(any(UserPrincipal.class))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(UserPrincipal.class))).thenReturn("refresh-token");
        when(userService.getUserProfile(1L)).thenReturn(Optional.empty());

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("使用者名稱或密碼錯誤");
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        
        when(jwtUtil.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("testuser");
        when(userService.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(UserPrincipal.class))).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(any(UserPrincipal.class))).thenReturn("new-refresh-token");
        when(userService.getUserProfile(1L)).thenReturn(Optional.empty());

        // When
        AuthResponse response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Given
        String invalidToken = "invalid-token";
        
        when(jwtUtil.isTokenValid(invalidToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("無效的 Refresh Token");
    }

    @Test
    void testRefreshToken_NotRefreshToken() {
        // Given
        String accessToken = "access-token";
        
        when(jwtUtil.isTokenValid(accessToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(accessToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(accessToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("無效的 Refresh Token");
    }

    @Test
    void testValidateToken_ValidToken() {
        // Given
        String token = "valid-token";
        
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userService.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(testUser));

        // When
        boolean isValid = authService.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Given
        String token = "invalid-token";
        
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        // When
        boolean isValid = authService.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateToken_UserNotFound() {
        // Given
        String token = "valid-token";
        
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("nonexistentuser");
        when(userService.findByUsernameOrEmail("nonexistentuser")).thenReturn(Optional.empty());

        // When
        boolean isValid = authService.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testIsUsernameAvailable() {
        // Given
        when(userService.isUsernameAvailable("availableuser")).thenReturn(true);
        when(userService.isUsernameAvailable("takenuser")).thenReturn(false);

        // When & Then
        assertThat(authService.isUsernameAvailable("availableuser")).isTrue();
        assertThat(authService.isUsernameAvailable("takenuser")).isFalse();
    }

    @Test
    void testIsEmailAvailable() {
        // Given
        when(userService.isEmailAvailable("available@example.com")).thenReturn(true);
        when(userService.isEmailAvailable("taken@example.com")).thenReturn(false);

        // When & Then
        assertThat(authService.isEmailAvailable("available@example.com")).isTrue();
        assertThat(authService.isEmailAvailable("taken@example.com")).isFalse();
    }
}

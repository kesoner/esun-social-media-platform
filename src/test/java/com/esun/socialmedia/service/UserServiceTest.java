package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.user.UpdateProfileRequest;
import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.repository.CommentRepository;
import com.esun.socialmedia.repository.PostRepository;
import com.esun.socialmedia.repository.UserRepository;
import com.esun.socialmedia.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService 測試類別
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setBiography("測試使用者");
    }

    @Test
    void testCreateUser_Success() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordService.validatePassword("password123")).thenReturn(null);
        when(passwordService.encodePassword("password123")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser("newuser", "new@example.com", "password123", "新使用者");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Given
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser("existinguser", "new@example.com", "password123", "新使用者"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("使用者名稱已存在");
    }

    @Test
    void testCreateUser_EmailExists() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser("newuser", "existing@example.com", "password123", "新使用者"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("電子郵件已存在");
    }

    @Test
    void testCreateUser_WeakPassword() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordService.validatePassword("weak")).thenReturn("密碼太弱");

        // When & Then
        assertThatThrownBy(() -> userService.createUser("newuser", "new@example.com", "weak", "新使用者"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("密碼太弱");
    }

    @Test
    void testGetUserProfile() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.countByAuthorId(1L)).thenReturn(5L);
        when(commentRepository.countByAuthorId(1L)).thenReturn(10L);

        // When
        Optional<UserResponse> result = userService.getUserProfile(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getPostCount()).isEqualTo(5L);
        assertThat(result.get().getCommentCount()).isEqualTo(10L);
    }

    @Test
    void testUpdateProfile_Success() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("updateduser");
        request.setBiography("更新的簡介");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsernameAndIdNot("updateduser", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(postRepository.countByAuthorId(1L)).thenReturn(5L);
        when(commentRepository.countByAuthorId(1L)).thenReturn(10L);

        // When
        UserResponse result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UsernameExists() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsernameAndIdNot("existinguser", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateProfile(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("使用者名稱已存在");
    }

    @Test
    void testFindByUsername() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testIsUsernameAvailable() {
        // Given
        when(userRepository.existsByUsername("availableuser")).thenReturn(false);
        when(userRepository.existsByUsername("takenuser")).thenReturn(true);

        // When & Then
        assertThat(userService.isUsernameAvailable("availableuser")).isTrue();
        assertThat(userService.isUsernameAvailable("takenuser")).isFalse();
    }

    @Test
    void testValidatePassword() {
        // Given
        when(passwordService.matches("correctpassword", "hashedpassword")).thenReturn(true);
        when(passwordService.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        // When & Then
        assertThat(userService.validatePassword(testUser, "correctpassword")).isTrue();
        assertThat(userService.validatePassword(testUser, "wrongpassword")).isFalse();
    }
}

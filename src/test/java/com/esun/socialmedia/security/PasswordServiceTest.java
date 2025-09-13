package com.esun.socialmedia.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * PasswordService 測試類別
 */
class PasswordServiceTest {

    private PasswordService passwordService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        passwordService = new PasswordService(passwordEncoder);
    }

    @Test
    void testEncodePassword() {
        // Given
        String rawPassword = "password123";

        // When
        String encodedPassword = passwordService.encodePassword(rawPassword);

        // Then
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encodedPassword).startsWith("$2a$");
    }

    @Test
    void testEncodePasswordWithNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.encodePassword(null);
        });
    }

    @Test
    void testEncodePasswordWithEmpty() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.encodePassword("");
        });
    }

    @Test
    void testMatches() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = passwordService.encodePassword(rawPassword);

        // When
        boolean matches = passwordService.matches(rawPassword, encodedPassword);

        // Then
        assertThat(matches).isTrue();
    }

    @Test
    void testMatchesWithWrongPassword() {
        // Given
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = passwordService.encodePassword(rawPassword);

        // When
        boolean matches = passwordService.matches(wrongPassword, encodedPassword);

        // Then
        assertThat(matches).isFalse();
    }

    @Test
    void testIsPasswordStrong() {
        // Valid passwords
        assertThat(passwordService.isPasswordStrong("password123")).isTrue();
        assertThat(passwordService.isPasswordStrong("MyPass123")).isTrue();
        assertThat(passwordService.isPasswordStrong("test1234")).isTrue();
        assertThat(passwordService.isPasswordStrong("Password1@")).isTrue();

        // Invalid passwords
        assertThat(passwordService.isPasswordStrong("password")).isFalse(); // no number
        assertThat(passwordService.isPasswordStrong("12345678")).isFalse(); // no letter
        assertThat(passwordService.isPasswordStrong("pass123")).isFalse(); // too short
        assertThat(passwordService.isPasswordStrong(null)).isFalse(); // null
        assertThat(passwordService.isPasswordStrong("")).isFalse(); // empty
    }

    @Test
    void testValidatePassword() {
        // Valid passwords
        assertThat(passwordService.validatePassword("password123")).isNull();
        assertThat(passwordService.validatePassword("MyPass123")).isNull();

        // Invalid passwords
        assertThat(passwordService.validatePassword(null)).isEqualTo("密碼不能為空");
        assertThat(passwordService.validatePassword("")).isEqualTo("密碼不能為空");
        assertThat(passwordService.validatePassword("pass123")).isEqualTo("密碼長度至少需要 8 字元");
        assertThat(passwordService.validatePassword("password")).isEqualTo("密碼必須包含至少一個數字");
        assertThat(passwordService.validatePassword("12345678")).isEqualTo("密碼必須包含至少一個英文字母");
    }

    @Test
    void testGenerateRandomPassword() {
        // When
        String password = passwordService.generateRandomPassword(12);

        // Then
        assertThat(password).isNotNull();
        assertThat(password.length()).isEqualTo(12);
        assertThat(passwordService.isPasswordStrong(password)).isTrue();
    }

    @Test
    void testGenerateRandomPasswordMinLength() {
        // When
        String password = passwordService.generateRandomPassword(5); // less than 8

        // Then
        assertThat(password).isNotNull();
        assertThat(password.length()).isEqualTo(8); // should be adjusted to 8
        assertThat(passwordService.isPasswordStrong(password)).isTrue();
    }

    @Test
    void testGetPasswordRequirements() {
        // When
        String requirements = passwordService.getPasswordRequirements();

        // Then
        assertThat(requirements).isNotNull();
        assertThat(requirements).contains("8 字元");
        assertThat(requirements).contains("英文字母");
        assertThat(requirements).contains("數字");
    }
}

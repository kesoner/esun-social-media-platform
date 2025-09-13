package com.esun.socialmedia.integration;

import com.esun.socialmedia.dto.auth.LoginRequest;
import com.esun.socialmedia.dto.auth.RegisterRequest;
import com.esun.socialmedia.dto.user.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 整合測試
 */
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setBiography("新使用者");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.username").value("newuser"))
                .andExpect(jsonPath("$.user.email").value("newuser@example.com"));
    }

    @Test
    void testRegister_DuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser"); // 已存在的使用者名稱
        request.setEmail("another@example.com");
        request.setPassword("password123");
        request.setBiography("重複使用者");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testRegister_InvalidInput() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // 空的使用者名稱
        request.setEmail("invalid-email"); // 無效的電子郵件
        request.setPassword("123"); // 太短的密碼

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("nonexistentuser");
        request.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetProfile_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/profile")
                .header("Authorization", createAuthHeader(testUserToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.biography").value("測試使用者"));
    }

    @Test
    void testGetProfile_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("updateduser");
        request.setBiography("更新的簡介");

        mockMvc.perform(MockMvcRequestBuilders.put("/auth/profile")
                .header("Authorization", createAuthHeader(testUserToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.biography").value("更新的簡介"));
    }

    @Test
    void testCheckUsername_Available() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/check-username")
                .param("username", "availableuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testCheckUsername_Taken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/check-username")
                .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testCheckEmail_Available() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/check-email")
                .param("email", "available@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testCheckEmail_Taken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/check-email")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                .header("Authorization", createAuthHeader(testUserToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登出成功"));
    }
}

package com.esun.socialmedia.integration;

import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.repository.CommentRepository;
import com.esun.socialmedia.repository.PostRepository;
import com.esun.socialmedia.repository.UserRepository;
import com.esun.socialmedia.security.JwtUtil;
import com.esun.socialmedia.security.PasswordService;
import com.esun.socialmedia.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 整合測試基礎類別
 * 
 * 提供整合測試所需的共用設定和工具方法
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected PasswordService passwordService;

    protected User testUser;
    protected String testUserToken;

    @BeforeEach
    void setUpBaseTest() {
        // 清理測試資料
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        // 建立測試使用者
        testUser = createTestUser("testuser", "test@example.com", "password123");
        testUserToken = generateTokenForUser(testUser);
    }

    /**
     * 建立測試使用者
     */
    protected User createTestUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordService.encodePassword(password));
        user.setBiography("測試使用者");
        return userRepository.save(user);
    }

    /**
     * 為使用者產生 JWT Token
     */
    protected String generateTokenForUser(User user) {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        return jwtUtil.generateToken(userPrincipal);
    }

    /**
     * 建立 Authorization Header
     */
    protected String createAuthHeader(String token) {
        return "Bearer " + token;
    }

    /**
     * 將物件轉換為 JSON 字串
     */
    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 將 JSON 字串轉換為物件
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}

package com.esun.socialmedia.integration;

import com.esun.socialmedia.dto.post.CreatePostRequest;
import com.esun.socialmedia.dto.post.UpdatePostRequest;
import com.esun.socialmedia.entity.Post;
import com.esun.socialmedia.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PostController 整合測試
 */
class PostControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void testCreatePost_Success() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("這是一篇測試發文");

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                .header("Authorization", createAuthHeader(testUserToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("這是一篇測試發文"))
                .andExpect(jsonPath("$.author.username").value("testuser"))
                .andExpect(jsonPath("$.commentCount").value(0));
    }

    @Test
    void testCreatePost_Unauthorized() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("這是一篇測試發文");

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreatePost_InvalidInput() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent(""); // 空內容

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                .header("Authorization", createAuthHeader(testUserToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void testGetAllPosts() throws Exception {
        // 建立測試發文
        Post post = createTestPost("測試發文內容");

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value("測試發文內容"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetPostById_Success() throws Exception {
        Post post = createTestPost("測試發文內容");

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.content").value("測試發文內容"))
                .andExpect(jsonPath("$.author.username").value("testuser"));
    }

    @Test
    void testGetPostById_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePost_Success() throws Exception {
        Post post = createTestPost("原始內容");
        
        UpdatePostRequest request = new UpdatePostRequest();
        request.setContent("更新後的內容");

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/{id}", post.getId())
                .header("Authorization", createAuthHeader(testUserToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("更新後的內容"));
    }

    @Test
    void testUpdatePost_NotAuthor() throws Exception {
        // 建立另一個使用者和發文
        User anotherUser = createTestUser("anotheruser", "another@example.com", "password123");
        Post post = createTestPostByUser("其他人的發文", anotherUser);
        
        UpdatePostRequest request = new UpdatePostRequest();
        request.setContent("嘗試更新他人發文");

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/{id}", post.getId())
                .header("Authorization", createAuthHeader(testUserToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeletePost_Success() throws Exception {
        Post post = createTestPost("要刪除的發文");

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{id}", post.getId())
                .header("Authorization", createAuthHeader(testUserToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("發文刪除成功"));
    }

    @Test
    void testDeletePost_NotAuthor() throws Exception {
        User anotherUser = createTestUser("anotheruser", "another@example.com", "password123");
        Post post = createTestPostByUser("其他人的發文", anotherUser);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{id}", post.getId())
                .header("Authorization", createAuthHeader(testUserToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPostsByAuthor() throws Exception {
        createTestPost("使用者的發文1");
        createTestPost("使用者的發文2");

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/author/{authorId}", testUser.getId())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testSearchPosts() throws Exception {
        createTestPost("這是關於 Java 的發文");
        createTestPost("這是關於 Python 的發文");

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/search")
                .param("keyword", "Java")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].content").value("這是關於 Java 的發文"));
    }

    @Test
    void testGetLatestPosts() throws Exception {
        createTestPost("最新發文1");
        createTestPost("最新發文2");
        createTestPost("最新發文3");

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/latest")
                .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * 建立測試發文
     */
    private Post createTestPost(String content) {
        return createTestPostByUser(content, testUser);
    }

    /**
     * 為指定使用者建立測試發文
     */
    private Post createTestPostByUser(String content, User author) {
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);
        return postRepository.save(post);
    }
}

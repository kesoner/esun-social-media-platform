package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.post.CreatePostRequest;
import com.esun.socialmedia.dto.post.PostResponse;
import com.esun.socialmedia.dto.post.UpdatePostRequest;
import com.esun.socialmedia.entity.Post;
import com.esun.socialmedia.entity.User;
import com.esun.socialmedia.repository.CommentRepository;
import com.esun.socialmedia.repository.PostRepository;
import com.esun.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * PostService 測試類別
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setContent("測試發文內容");
        testPost.setAuthor(testUser);
    }

    @Test
    void testCreatePost_Success() {
        // Given
        CreatePostRequest request = new CreatePostRequest("新發文內容");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        PostResponse result = postService.createPost(request, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("測試發文內容");
        assertThat(result.getAuthor().getUsername()).isEqualTo("testuser");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testCreatePost_UserNotFound() {
        // Given
        CreatePostRequest request = new CreatePostRequest("新發文內容");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.createPost(request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到使用者");
    }

    @Test
    void testGetPostById_Success() {
        // Given
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.countByPostId(1L)).thenReturn(5L);

        // When
        Optional<PostResponse> result = postService.getPostById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCommentCount()).isEqualTo(5L);
    }

    @Test
    void testGetPostById_NotFound() {
        // Given
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.empty());

        // When
        Optional<PostResponse> result = postService.getPostById(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllPosts() {
        // Given
        List<Post> posts = List.of(testPost);
        Page<Post> postPage = new PageImpl<>(posts);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(postRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(postPage);
        when(commentRepository.countByPostId(anyLong())).thenReturn(3L);

        // When
        Page<PostResponse> result = postService.getAllPosts(0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCommentCount()).isEqualTo(3L);
    }

    @Test
    void testUpdatePost_Success() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest("更新的內容");
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(commentRepository.countByPostId(1L)).thenReturn(2L);

        // When
        PostResponse result = postService.updatePost(1L, request, 1L);

        // Then
        assertThat(result).isNotNull();
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testUpdatePost_NotAuthor() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest("更新的內容");
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(1L, request, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("沒有權限編輯此發文");
    }

    @Test
    void testDeletePost_Success() {
        // Given
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(testPost));

        // When
        postService.deletePost(1L, 1L);

        // Then
        verify(postRepository).delete(testPost);
    }

    @Test
    void testDeletePost_NotAuthor() {
        // Given
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("沒有權限刪除此發文");
    }

    @Test
    void testGetPostsByAuthor() {
        // Given
        List<Post> posts = List.of(testPost);
        Page<Post> postPage = new PageImpl<>(posts);
        
        when(postRepository.findByAuthorIdOrderByCreatedAtDesc(eq(1L), any(Pageable.class))).thenReturn(postPage);
        when(commentRepository.countByPostId(anyLong())).thenReturn(4L);

        // When
        Page<PostResponse> result = postService.getPostsByAuthor(1L, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCommentCount()).isEqualTo(4L);
    }

    @Test
    void testSearchPosts() {
        // Given
        List<Post> posts = List.of(testPost);
        Page<Post> postPage = new PageImpl<>(posts);
        
        when(postRepository.findByContentContaining(eq("測試"), any(Pageable.class))).thenReturn(postPage);
        when(commentRepository.countByPostId(anyLong())).thenReturn(1L);

        // When
        Page<PostResponse> result = postService.searchPosts("測試", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCommentCount()).isEqualTo(1L);
    }

    @Test
    void testIsPostAuthor() {
        // Given
        when(postRepository.isAuthor(1L, 1L)).thenReturn(true);
        when(postRepository.isAuthor(1L, 2L)).thenReturn(false);

        // When & Then
        assertThat(postService.isPostAuthor(1L, 1L)).isTrue();
        assertThat(postService.isPostAuthor(1L, 2L)).isFalse();
    }
}

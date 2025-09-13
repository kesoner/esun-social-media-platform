package com.esun.socialmedia.service;

import com.esun.socialmedia.dto.comment.CommentResponse;
import com.esun.socialmedia.dto.comment.CreateCommentRequest;
import com.esun.socialmedia.entity.Comment;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CommentService 測試類別
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;

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

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("測試留言內容");
        testComment.setPost(testPost);
        testComment.setAuthor(testUser);
    }

    @Test
    void testCreateComment_Success() {
        // Given
        CreateCommentRequest request = new CreateCommentRequest("新留言內容");
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // When
        CommentResponse result = commentService.createComment(1L, request, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("測試留言內容");
        assertThat(result.getAuthor().getUsername()).isEqualTo("testuser");
        assertThat(result.getPostId()).isEqualTo(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testCreateComment_PostNotFound() {
        // Given
        CreateCommentRequest request = new CreateCommentRequest("新留言內容");
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(1L, request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到發文");
    }

    @Test
    void testCreateComment_UserNotFound() {
        // Given
        CreateCommentRequest request = new CreateCommentRequest("新留言內容");
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(1L, request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到使用者");
    }

    @Test
    void testGetCommentById_Success() {
        // Given
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(testComment));

        // When
        Optional<CommentResponse> result = commentService.getCommentById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getContent()).isEqualTo("測試留言內容");
    }

    @Test
    void testGetCommentById_NotFound() {
        // Given
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.empty());

        // When
        Optional<CommentResponse> result = commentService.getCommentById(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testGetCommentsByPostId() {
        // Given
        List<Comment> comments = List.of(testComment);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).thenReturn(comments);

        // When
        List<CommentResponse> result = commentService.getCommentsByPostId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("測試留言內容");
    }

    @Test
    void testDeleteComment_Success_CommentAuthor() {
        // Given
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(testComment));

        // When
        commentService.deleteComment(1L, 1L);

        // Then
        verify(commentRepository).delete(testComment);
    }

    @Test
    void testDeleteComment_Success_PostAuthor() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        testComment.setAuthor(anotherUser);
        
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(testComment));

        // When
        commentService.deleteComment(1L, 1L); // 發文作者刪除留言

        // Then
        verify(commentRepository).delete(testComment);
    }

    @Test
    void testDeleteComment_NoPermission() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        testComment.setAuthor(anotherUser);
        
        User postAuthor = new User();
        postAuthor.setId(3L);
        testPost.setAuthor(postAuthor);
        
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(testComment));

        // When & Then
        assertThatThrownBy(() -> commentService.deleteComment(1L, 4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("沒有權限刪除此留言");
    }

    @Test
    void testDeleteComment_NotFound() {
        // Given
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.deleteComment(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到留言");
    }

    @Test
    void testCountCommentsByPostId() {
        // Given
        when(commentRepository.countByPostId(1L)).thenReturn(5L);

        // When
        long result = commentService.countCommentsByPostId(1L);

        // Then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    void testCountCommentsByAuthor() {
        // Given
        when(commentRepository.countByAuthorId(1L)).thenReturn(10L);

        // When
        long result = commentService.countCommentsByAuthor(1L);

        // Then
        assertThat(result).isEqualTo(10L);
    }

    @Test
    void testIsCommentAuthor() {
        // Given
        when(commentRepository.isAuthor(1L, 1L)).thenReturn(true);
        when(commentRepository.isAuthor(1L, 2L)).thenReturn(false);

        // When & Then
        assertThat(commentService.isCommentAuthor(1L, 1L)).isTrue();
        assertThat(commentService.isCommentAuthor(1L, 2L)).isFalse();
    }

    @Test
    void testCanDeleteComment() {
        // Given
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(testComment));

        // When & Then
        assertThat(commentService.canDeleteComment(1L, 1L)).isTrue(); // 留言作者
        assertThat(commentService.canDeleteComment(1L, 2L)).isFalse(); // 其他使用者
    }
}

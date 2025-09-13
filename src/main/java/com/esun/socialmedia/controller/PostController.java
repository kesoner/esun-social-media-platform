package com.esun.socialmedia.controller;

import com.esun.socialmedia.dto.post.CreatePostRequest;
import com.esun.socialmedia.dto.post.PostResponse;
import com.esun.socialmedia.dto.post.UpdatePostRequest;
import com.esun.socialmedia.security.UserPrincipal;
import com.esun.socialmedia.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 發文控制器
 * 
 * 處理發文相關的 HTTP 請求
 * 
 * @author 開發團隊
 */
@RestController
@RequestMapping("/posts")
@Tag(name = "發文管理", description = "發文的 CRUD 操作相關 API")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 建立新發文
     */
    @PostMapping
    @Operation(summary = "建立發文", description = "建立新的發文")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "建立成功"),
        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<PostResponse> createPost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreatePostRequest request) {
        
        PostResponse response = postService.createPost(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 獲取所有發文
     */
    @GetMapping
    @Operation(summary = "獲取發文列表", description = "獲取所有發文的分頁列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<PostResponse> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    /**
     * 根據 ID 獲取發文
     */
    @GetMapping("/{id}")
    @Operation(summary = "獲取發文詳情", description = "根據 ID 獲取發文的詳細資訊")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功"),
        @ApiResponse(responseCode = "404", description = "發文不存在")
    })
    public ResponseEntity<PostResponse> getPostById(
            @Parameter(description = "發文 ID") @PathVariable Long id) {
        
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新發文
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新發文", description = "更新指定的發文（僅作者可操作）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "沒有權限"),
        @ApiResponse(responseCode = "404", description = "發文不存在")
    })
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "發文 ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdatePostRequest request) {
        
        try {
            PostResponse response = postService.updatePost(id, request, currentUser.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("找不到發文")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("沒有權限")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * 刪除發文
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除發文", description = "刪除指定的發文（僅作者可操作）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "刪除成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "沒有權限"),
        @ApiResponse(responseCode = "404", description = "發文不存在")
    })
    public ResponseEntity<Map<String, String>> deletePost(
            @Parameter(description = "發文 ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            postService.deletePost(id, currentUser.getId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "發文刪除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("找不到發文")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("沒有權限")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * 根據作者獲取發文
     */
    @GetMapping("/author/{authorId}")
    @Operation(summary = "獲取使用者發文", description = "獲取指定使用者的發文列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<Page<PostResponse>> getPostsByAuthor(
            @Parameter(description = "作者 ID") @PathVariable Long authorId,
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<PostResponse> posts = postService.getPostsByAuthor(authorId, page, size);
        return ResponseEntity.ok(posts);
    }

    /**
     * 搜尋發文
     */
    @GetMapping("/search")
    @Operation(summary = "搜尋發文", description = "根據關鍵字搜尋發文")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜尋完成")
    })
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @Parameter(description = "搜尋關鍵字") @RequestParam String keyword,
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        }
        
        Page<PostResponse> posts = postService.searchPosts(keyword.trim(), page, size);
        return ResponseEntity.ok(posts);
    }

    /**
     * 獲取熱門發文
     */
    @GetMapping("/popular")
    @Operation(summary = "獲取熱門發文", description = "獲取熱門發文列表（根據留言數量排序）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<Page<PostResponse>> getPopularPosts(
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<PostResponse> posts = postService.getPopularPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    /**
     * 獲取最新發文
     */
    @GetMapping("/latest")
    @Operation(summary = "獲取最新發文", description = "獲取最新的發文列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<List<PostResponse>> getLatestPosts(
            @Parameter(description = "數量限制") @RequestParam(defaultValue = "5") int limit) {
        
        List<PostResponse> posts = postService.getLatestPosts(limit);
        return ResponseEntity.ok(posts);
    }
}

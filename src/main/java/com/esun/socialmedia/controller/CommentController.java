package com.esun.socialmedia.controller;

import com.esun.socialmedia.dto.comment.CommentResponse;
import com.esun.socialmedia.dto.comment.CreateCommentRequest;
import com.esun.socialmedia.security.UserPrincipal;
import com.esun.socialmedia.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 留言控制器
 * 
 * 處理留言相關的 HTTP 請求
 * 
 * @author 開發團隊
 */
@RestController
@RequestMapping("/comments")
@Tag(name = "留言管理", description = "留言的新增、查詢、刪除相關 API")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 建立新留言
     */
    @PostMapping("/posts/{postId}")
    @Operation(summary = "建立留言", description = "為指定發文建立新留言")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "建立成功"),
        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "發文不存在")
    })
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "發文 ID") @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateCommentRequest request) {
        
        try {
            CommentResponse response = commentService.createComment(postId, request, currentUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("找不到發文")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * 根據 ID 獲取留言
     */
    @GetMapping("/{id}")
    @Operation(summary = "獲取留言詳情", description = "根據 ID 獲取留言的詳細資訊")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功"),
        @ApiResponse(responseCode = "404", description = "留言不存在")
    })
    public ResponseEntity<CommentResponse> getCommentById(
            @Parameter(description = "留言 ID") @PathVariable Long id) {
        
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根據發文 ID 獲取留言列表
     */
    @GetMapping("/posts/{postId}")
    @Operation(summary = "獲取發文留言", description = "獲取指定發文的所有留言")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(
            @Parameter(description = "發文 ID") @PathVariable Long postId) {
        
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 根據發文 ID 獲取留言列表（分頁）
     */
    @GetMapping("/posts/{postId}/page")
    @Operation(summary = "獲取發文留言（分頁）", description = "獲取指定發文的留言分頁列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<Page<CommentResponse>> getCommentsByPostIdPaged(
            @Parameter(description = "發文 ID") @PathVariable Long postId,
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<CommentResponse> comments = commentService.getCommentsByPostId(postId, page, size);
        return ResponseEntity.ok(comments);
    }

    /**
     * 根據作者獲取留言列表
     */
    @GetMapping("/author/{authorId}")
    @Operation(summary = "獲取使用者留言", description = "獲取指定使用者的留言列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<Page<CommentResponse>> getCommentsByAuthor(
            @Parameter(description = "作者 ID") @PathVariable Long authorId,
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<CommentResponse> comments = commentService.getCommentsByAuthor(authorId, page, size);
        return ResponseEntity.ok(comments);
    }

    /**
     * 刪除留言
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除留言", description = "刪除指定的留言（留言作者或發文作者可操作）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "刪除成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "403", description = "沒有權限"),
        @ApiResponse(responseCode = "404", description = "留言不存在")
    })
    public ResponseEntity<Map<String, String>> deleteComment(
            @Parameter(description = "留言 ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            commentService.deleteComment(id, currentUser.getId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "留言刪除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("找不到留言")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("沒有權限")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * 搜尋留言
     */
    @GetMapping("/search")
    @Operation(summary = "搜尋留言", description = "根據關鍵字搜尋留言")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜尋完成")
    })
    public ResponseEntity<Page<CommentResponse>> searchComments(
            @Parameter(description = "搜尋關鍵字") @RequestParam String keyword,
            @Parameter(description = "頁碼（從 0 開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        }
        
        Page<CommentResponse> comments = commentService.searchComments(keyword.trim(), page, size);
        return ResponseEntity.ok(comments);
    }

    /**
     * 獲取最新留言
     */
    @GetMapping("/latest")
    @Operation(summary = "獲取最新留言", description = "獲取最新的留言列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功")
    })
    public ResponseEntity<List<CommentResponse>> getLatestComments(
            @Parameter(description = "數量限制") @RequestParam(defaultValue = "10") int limit) {
        
        List<CommentResponse> comments = commentService.getLatestComments(limit);
        return ResponseEntity.ok(comments);
    }

    /**
     * 統計發文的留言數量
     */
    @GetMapping("/posts/{postId}/count")
    @Operation(summary = "統計發文留言數", description = "統計指定發文的留言數量")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "統計完成")
    })
    public ResponseEntity<Map<String, Long>> countCommentsByPostId(
            @Parameter(description = "發文 ID") @PathVariable Long postId) {
        
        long count = commentService.countCommentsByPostId(postId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}

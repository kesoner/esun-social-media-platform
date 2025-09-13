package com.esun.socialmedia.controller;

import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 使用者控制器
 * 
 * 處理使用者查詢相關請求
 * 
 * @author 開發團隊
 */
@RestController
@RequestMapping("/users")
@Tag(name = "使用者管理", description = "使用者查詢相關 API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 根據 ID 獲取使用者資訊
     */
    @GetMapping("/{id}")
    @Operation(summary = "獲取使用者資訊", description = "根據使用者 ID 獲取使用者詳細資訊")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功"),
        @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "使用者 ID") @PathVariable Long id) {
        
        return userService.getUserProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根據使用者名稱獲取使用者資訊
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "根據使用者名稱獲取使用者", description = "根據使用者名稱獲取使用者詳細資訊")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功"),
        @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "使用者名稱") @PathVariable String username) {
        
        return userService.findByUsername(username)
                .map(user -> userService.getUserProfile(user.getId()))
                .orElse(java.util.Optional.empty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 搜尋使用者
     */
    @GetMapping("/search")
    @Operation(summary = "搜尋使用者", description = "根據關鍵字搜尋使用者")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜尋完成")
    })
    public ResponseEntity<List<UserResponse>> searchUsers(
            @Parameter(description = "搜尋關鍵字") @RequestParam String keyword) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<UserResponse> users = userService.searchUsers(keyword.trim());
        return ResponseEntity.ok(users);
    }
}

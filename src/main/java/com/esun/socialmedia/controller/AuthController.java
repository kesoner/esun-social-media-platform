package com.esun.socialmedia.controller;

import com.esun.socialmedia.dto.auth.AuthResponse;
import com.esun.socialmedia.dto.auth.LoginRequest;
import com.esun.socialmedia.dto.auth.RegisterRequest;
import com.esun.socialmedia.dto.user.UpdateProfileRequest;
import com.esun.socialmedia.dto.user.UserResponse;
import com.esun.socialmedia.security.UserPrincipal;
import com.esun.socialmedia.service.AuthService;
import com.esun.socialmedia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 認證控制器
 * 
 * 處理使用者註冊、登入、個人資料管理等請求
 * 
 * @author 開發團隊
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "認證管理", description = "使用者註冊、登入、個人資料管理相關 API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * 使用者註冊
     */
    @PostMapping("/register")
    @Operation(summary = "使用者註冊", description = "建立新的使用者帳號")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "註冊成功"),
        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
        @ApiResponse(responseCode = "409", description = "使用者名稱或電子郵件已存在")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * 使用者登入
     */
    @PostMapping("/login")
    @Operation(summary = "使用者登入", description = "使用使用者名稱/電子郵件和密碼登入")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登入成功"),
        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
        @ApiResponse(responseCode = "401", description = "使用者名稱或密碼錯誤")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 獲取新的 Access Token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "刷新成功"),
        @ApiResponse(responseCode = "400", description = "無效的 Refresh Token")
    })
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 獲取當前使用者資訊
     */
    @GetMapping("/profile")
    @Operation(summary = "獲取個人資料", description = "獲取當前登入使用者的詳細資訊")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "獲取成功"),
        @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<UserResponse> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser) {
        
        UserResponse userResponse = userService.getUserProfile(currentUser.getId())
                .orElse(UserResponse.from(currentUser.getUser()));
        
        return ResponseEntity.ok(userResponse);
    }

    /**
     * 更新個人資料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新個人資料", description = "更新當前使用者的個人資料")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "409", description = "使用者名稱或電子郵件已存在")
    })
    public ResponseEntity<UserResponse> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        try {
            UserResponse response = userService.updateProfile(currentUser.getId(), request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * 檢查使用者名稱是否可用
     */
    @GetMapping("/check-username")
    @Operation(summary = "檢查使用者名稱", description = "檢查使用者名稱是否可用")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "檢查完成")
    })
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean available = authService.isUsernameAvailable(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        return ResponseEntity.ok(response);
    }

    /**
     * 檢查電子郵件是否可用
     */
    @GetMapping("/check-email")
    @Operation(summary = "檢查電子郵件", description = "檢查電子郵件是否可用")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "檢查完成")
    })
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean available = authService.isEmailAvailable(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        return ResponseEntity.ok(response);
    }

    /**
     * 登出（客戶端處理）
     */
    @PostMapping("/logout")
    @Operation(summary = "使用者登出", description = "登出當前使用者（客戶端需清除 Token）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登出成功")
    })
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }
}

package com.dataplatform.user.controller;

import com.dataplatform.user.dto.*;
import com.dataplatform.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "用户管理", description = "用户注册、登录、个人信息及管理员CRUD接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "注册新用户，默认角色为 user")
    @PostMapping("/register")
    public UserProfileResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserProfileResponse.builder()
                .id(userService.register(request).getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role("user")
                .status(1)
                .build();
    }

    @Operation(summary = "用户登录", description = "返回 JWT access token 和 refresh token")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/profile")
    public UserProfileResponse getProfile(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return userService.getProfile(userId);
    }

    @Operation(summary = "更新当前用户信息", security = @SecurityRequirement(name = "BearerAuth"))
    @PutMapping("/profile")
    public UserProfileResponse updateProfile(HttpServletRequest request,
                                              @Valid @RequestBody UpdateProfileRequest updateRequest) {
        Long userId = extractUserId(request);
        return userService.updateProfile(userId, updateRequest);
    }

    @Operation(summary = "修改密码", security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/password")
    public void changePassword(HttpServletRequest request,
                               @Valid @RequestBody PasswordChangeRequest passwordRequest) {
        Long userId = extractUserId(request);
        userService.changePassword(userId, passwordRequest);
    }

    @Operation(summary = "刷新 Token", description = "使用 refresh token 换取新的 access token")
    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        return userService.refreshToken(refreshRequest.getRefreshToken());
    }

    @Operation(summary = "健康检查", description = "服务存活探测")
    @GetMapping("/actuator/health")
    public String health() {
        return "OK";
    }

    // ==================== 管理员专用接口 ====================

    /**
     * 获取所有用户列表（管理员）
     */
    @Operation(summary = "获取所有用户列表", description = "仅管理员可调用",
            security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping
    public List<UserProfileResponse> listUsers(HttpServletRequest request) {
        requireAdmin(request);
        return userService.listAllUsers();
    }

    /**
     * 创建新用户（管理员）
     */
    @Operation(summary = "创建用户（管理员）", description = "管理员创建新用户，可指定角色",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/admin/create")
    public UserProfileResponse createUser(HttpServletRequest request,
                                          @Valid @RequestBody CreateUserRequest createRequest) {
        requireAdmin(request);
        return userService.createUser(createRequest);
    }

    /**
     * 更新用户信息和角色（管理员）
     */
    @Operation(summary = "更新用户信息（管理员）", description = "可修改邮箱、手机、状态和角色",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PutMapping("/{id}")
    public UserProfileResponse updateUser(HttpServletRequest request,
                                          @Parameter(description = "用户ID") @PathVariable Long id,
                                          @RequestBody UpdateUserRequest updateRequest) {
        requireAdmin(request);
        return userService.updateUser(id, updateRequest);
    }

    /**
     * 删除用户（管理员，软删除）
     */
    @Operation(summary = "删除用户（管理员）", description = "软删除，不可恢复",
            security = @SecurityRequirement(name = "BearerAuth"))
    @DeleteMapping("/{id}")
    public Map<String, String> deleteUser(HttpServletRequest request,
                                          @Parameter(description = "用户ID") @PathVariable Long id) {
        requireAdmin(request);
        userService.deleteUser(id);
        Map<String, String> result = new HashMap<>();
        result.put("message", "User deleted");
        return result;
    }

    /**
     * 启用或禁用用户（管理员）
     */
    @Operation(summary = "切换用户状态（管理员）", description = "body: {\"status\": 1} 启用，{\"status\": 0} 禁用",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PutMapping("/{id}/toggle")
    public UserProfileResponse toggleUser(HttpServletRequest request,
                                          @Parameter(description = "用户ID") @PathVariable Long id,
                                          @RequestBody Map<String, Integer> body) {
        requireAdmin(request);
        Integer status = body.get("status");
        if (status == null) {
            status = body.getOrDefault("enabled", 1) == 1 ? 1 : 0;
        }
        return userService.toggleUserStatus(id, status);
    }

    // ==================== 私有方法 ====================

    private Long extractUserId(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = userService.validateToken(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }
        return userId;
    }

    /**
     * 校验当前请求是否为管理员身份
     * — 非管理员直接抛出异常
     */
    private void requireAdmin(HttpServletRequest request) {
        String token = extractToken(request);
        String role = userService.extractUserRole(token);
        if (!"admin".equals(role)) {
            throw new RuntimeException("Access denied: admin role required");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}


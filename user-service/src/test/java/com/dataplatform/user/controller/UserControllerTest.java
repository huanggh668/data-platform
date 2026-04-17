package com.dataplatform.user.controller;

import com.dataplatform.user.dto.*;
import com.dataplatform.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dataplatform.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 独立 MockMvc 测试（无 Spring 上下文）
 * <p>
 * 使用 standaloneSetup 避免加载 MyBatis/DataSource 等非相关 Bean
 *
 * @author system
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController MockMvc 测试")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String ADMIN_TOKEN = "admin.jwt.token";
    private static final String AUTH_HEADER = "Authorization";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ==================== 注册 ====================

    @Test
    @DisplayName("POST /register - 参数合法时注册成功，返回用户信息")
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@test.com");

        com.dataplatform.user.entity.User savedUser = new com.dataplatform.user.entity.User();
        savedUser.setId(100L);
        savedUser.setUsername("newuser");

        when(userService.register(any(RegisterRequest.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @DisplayName("POST /register - 用户名已存在时 service 抛出异常，返回 400")
    void register_usernameAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existUser");
        request.setPassword("password123");
        request.setEmail("exist@test.com");

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== 登录 ====================

    @Test
    @DisplayName("POST /login - 凭据正确时返回 Token")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        LoginResponse loginResponse = LoginResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .expiresIn(3600L)
                .build();

        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /login - 密码错误时 service 抛出异常，返回 400")
    void login_wrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid username or password"));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== 获取个人信息 ====================

    @Test
    @DisplayName("GET /profile - 携带有效 Token 时返回用户信息")
    void getProfile_withValidToken() throws Exception {
        when(userService.validateToken(VALID_TOKEN)).thenReturn(1L);

        UserProfileResponse profile = UserProfileResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("user")
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.getProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/v1/users/profile")
                        .header(AUTH_HEADER, "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /profile - 无 Authorization 头时返回 401")
    void getProfile_missingAuthHeader() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /profile - Token 无效时（validateToken 返回 null）返回 401")
    void getProfile_invalidToken() throws Exception {
        when(userService.validateToken("invalid")).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/profile")
                        .header(AUTH_HEADER, "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== 管理员接口 ====================

    @Test
    @DisplayName("GET /api/v1/users - 管理员 Token 时返回用户列表")
    void listUsers_asAdmin() throws Exception {
        when(userService.extractUserRole(ADMIN_TOKEN)).thenReturn("admin");

        List<UserProfileResponse> users = Arrays.asList(
                UserProfileResponse.builder().id(1L).username("user1").role("user").status(1).build(),
                UserProfileResponse.builder().id(2L).username("admin1").role("admin").status(1).build()
        );
        when(userService.listAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .header(AUTH_HEADER, "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/users - 普通用户 Token 时返回 401（Access denied）")
    void listUsers_asNonAdmin() throws Exception {
        when(userService.extractUserRole(VALID_TOKEN)).thenReturn("user");

        mockMvc.perform(get("/api/v1/users")
                        .header(AUTH_HEADER, "Bearer " + VALID_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /{id} - 管理员删除用户成功")
    void deleteUser_asAdmin() throws Exception {
        when(userService.extractUserRole(ADMIN_TOKEN)).thenReturn("admin");
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                        .header(AUTH_HEADER, "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted"));
    }

    @Test
    @DisplayName("PUT /{id}/toggle - 管理员禁用用户成功")
    void toggleUser_asAdmin() throws Exception {
        when(userService.extractUserRole(ADMIN_TOKEN)).thenReturn("admin");

        UserProfileResponse disabled = UserProfileResponse.builder()
                .id(1L).username("testuser").role("user").status(0).build();
        when(userService.toggleUserStatus(eq(1L), eq(0))).thenReturn(disabled);

        String body = "{\"status\": 0}";

        mockMvc.perform(put("/api/v1/users/1/toggle")
                        .header(AUTH_HEADER, "Bearer " + ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    // ==================== 健康检查 ====================

    @Test
    @DisplayName("GET /actuator/health - 无需认证返回 OK")
    void health() throws Exception {
        mockMvc.perform(get("/api/v1/users/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}

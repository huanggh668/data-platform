package com.dataplatform.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.user.dto.*;
import com.dataplatform.user.entity.User;
import com.dataplatform.user.repository.UserRepository;
import com.dataplatform.user.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 *
 * @author system
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setPassword("$2a$10$hashedpassword");
        existingUser.setEmail("test@example.com");
        existingUser.setPhone("13800138000");
        existingUser.setRole("user");
        existingUser.setStatus(1);
        existingUser.setCreatedAt(LocalDateTime.now());
        existingUser.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== 注册 ====================

    @Test
    @DisplayName("注册 - 用户名不存在时注册成功")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(true);

        User result = userService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("注册 - 用户名已存在时抛出异常")
    void register_usernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    // ==================== 登录 ====================

    @Test
    @DisplayName("登录 - 用户名密码正确时返回 Token")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("rawpassword");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);
        when(passwordEncoder.matches("rawpassword", existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn("refresh-token");
        when(jwtUtil.getExpiration()).thenReturn(3600L);

        LoginResponse response = userService.login(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("登录 - 用户不存在时抛出异常")
    void login_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("password");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    @DisplayName("登录 - 密码错误时抛出异常")
    void login_wrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    @DisplayName("登录 - 账户被禁用时抛出异常")
    void login_accountDisabled() {
        existingUser.setStatus(0);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("rawpassword");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("disabled");
    }

    // ==================== 获取用户信息 ====================

    @Test
    @DisplayName("getProfile - 用户存在时返回正确信息")
    void getProfile_success() {
        when(userRepository.getById(1L)).thenReturn(existingUser);

        UserProfileResponse profile = userService.getProfile(1L);

        assertThat(profile.getId()).isEqualTo(1L);
        assertThat(profile.getUsername()).isEqualTo("testuser");
        assertThat(profile.getEmail()).isEqualTo("test@example.com");
        assertThat(profile.getRole()).isEqualTo("user");
    }

    @Test
    @DisplayName("getProfile - 用户不存在时抛出异常")
    void getProfile_notFound() {
        when(userRepository.getById(999L)).thenReturn(null);

        assertThatThrownBy(() -> userService.getProfile(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    // ==================== 管理员功能 ====================

    @Test
    @DisplayName("createUser - 管理员创建用户成功")
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("adminCreated");
        request.setPassword("pass123");
        request.setEmail("admin@test.com");
        request.setRole("admin");

        when(userRepository.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(true);

        UserProfileResponse response = userService.createUser(request);

        assertThat(response.getRole()).isEqualTo("admin");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo("admin");
    }

    @Test
    @DisplayName("deleteUser - 用户存在时软删除成功")
    void deleteUser_success() {
        when(userRepository.getById(1L)).thenReturn(existingUser);
        when(userRepository.removeById(1L)).thenReturn(true);

        assertThatCode(() -> userService.deleteUser(1L)).doesNotThrowAnyException();
        verify(userRepository).removeById(1L);
    }

    @Test
    @DisplayName("deleteUser - 用户不存在时抛出异常")
    void deleteUser_notFound() {
        when(userRepository.getById(999L)).thenReturn(null);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(userRepository, never()).removeById(any(Long.class));
    }

    @Test
    @DisplayName("toggleUserStatus - 禁用用户状态更新正确")
    void toggleUserStatus_disable() {
        when(userRepository.getById(1L)).thenReturn(existingUser);
        when(userRepository.updateById(any(User.class))).thenReturn(true);

        UserProfileResponse response = userService.toggleUserStatus(1L, 0);

        assertThat(response.getStatus()).isEqualTo(0);
    }

    @Test
    @DisplayName("extractUserRole - Token 有效时返回用户角色")
    void extractUserRole_success() {
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userRepository.getById(1L)).thenReturn(existingUser);

        String role = userService.extractUserRole("valid-token");

        assertThat(role).isEqualTo("user");
    }

    @Test
    @DisplayName("extractUserRole - Token 无效时返回 null")
    void extractUserRole_invalidToken() {
        when(jwtUtil.extractUserId("bad-token")).thenReturn(null);

        String role = userService.extractUserRole("bad-token");

        assertThat(role).isNull();
    }
}

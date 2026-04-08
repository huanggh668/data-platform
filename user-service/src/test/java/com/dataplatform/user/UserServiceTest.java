package com.dataplatform.user;

import com.dataplatform.user.dto.LoginRequest;
import com.dataplatform.user.dto.LoginResponse;
import com.dataplatform.user.dto.RegisterRequest;
import com.dataplatform.user.entity.User;
import com.dataplatform.user.repository.UserRepository;
import com.dataplatform.user.service.UserService;
import com.dataplatform.user.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedpassword");
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setStatus(1);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(userRepository.exists(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$encodedpassword");
        when(userRepository.save(any())).thenReturn(testUser);

        User result = userService.register(request);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");

        when(userRepository.exists(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.getOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(1L, "testuser")).thenReturn("token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refreshToken");
        when(jwtUtil.getExpiration()).thenReturn(3600L);

        LoginResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userRepository.getOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    @Test
    void login_DisabledUser_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        testUser.setStatus(0);
        when(userRepository.getOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    @Test
    void getProfile_Success() {
        when(userRepository.getById(1L)).thenReturn(testUser);

        var response = userService.getProfile(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void getProfile_UserNotFound_ThrowsException() {
        when(userRepository.getById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.getProfile(999L));
    }

    @Test
    void validateToken_Success() {
        String token = "validToken";
        when(jwtUtil.extractUserId(token)).thenReturn(1L);

        Long userId = userService.validateToken(token);

        assertEquals(1L, userId);
    }

    @Test
    void validateToken_InvalidToken_ReturnsNull() {
        String token = "invalidToken";
        when(jwtUtil.extractUserId(token)).thenReturn(null);

        Long userId = userService.validateToken(token);

        assertNull(userId);
    }
}

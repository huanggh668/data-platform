package com.dataplatform.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.user.dto.*;
import com.dataplatform.user.entity.User;
import com.dataplatform.user.repository.UserRepository;
import com.dataplatform.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userRepository.getOne(wrapper) != null) {
            throw new RuntimeException("Username already exists");
        }

        if (request.getEmail() != null) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, request.getEmail());
            if (userRepository.getOne(emailWrapper) != null) {
                throw new RuntimeException("Email already exists");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return user;
    }

    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userRepository.getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("User account is disabled");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getExpiration())
                .user(toProfileResponse(user))
                .build();
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (request.getEmail() != null) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, request.getEmail())
                    .ne(User::getId, userId);
            if (userRepository.getOne(emailWrapper) != null) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);
    }

    public LoginResponse refreshToken(String refreshToken) {
        Claims claims = jwtUtil.validateToken(refreshToken);
        if (claims == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new RuntimeException("Invalid token type");
        }

        Long userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("User account is disabled");
        }

        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtUtil.getExpiration())
                .build();
    }

    public Long validateToken(String token) {
        return jwtUtil.extractUserId(token);
    }

    /**
     * 查询所有用户列表（管理员专用）
     *
     * @return 用户列表
     */
    public List<UserProfileResponse> listAllUsers() {
        return userRepository.list().stream()
                .map(this::toProfileResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建用户（管理员专用）
     *
     * @param request 创建请求
     * @return 新用户信息
     */
    @Transactional
    public UserProfileResponse createUser(CreateUserRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userRepository.getOne(wrapper) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : "user");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return toProfileResponse(user);
    }

    /**
     * 更新用户信息及角色（管理员专用）
     *
     * @param userId  目标用户ID
     * @param request 更新请求
     * @return 更新后用户信息
     */
    @Transactional
    public UserProfileResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);
        return toProfileResponse(user);
    }

    /**
     * 软删除用户（管理员专用）
     *
     * @param userId 目标用户ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        userRepository.removeById(userId);
    }

    /**
     * 启用或禁用用户
     *
     * @param userId 目标用户ID
     * @param status 1=启用，0=禁用
     * @return 更新后用户信息
     */
    @Transactional
    public UserProfileResponse toggleUserStatus(Long userId, Integer status) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);
        return toProfileResponse(user);
    }

    /** 提取当前操作者的角色，用于权限校验 */
    public String extractUserRole(String token) {
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) return null;
        User user = userRepository.getById(userId);
        return user != null ? user.getRole() : null;
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
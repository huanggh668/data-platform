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
        if (userRepository.selectCount(wrapper) > 0) {
            throw new RuntimeException("Username already exists");
        }

        if (request.getEmail() != null) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, request.getEmail());
            if (userRepository.selectCount(emailWrapper) > 0) {
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
                .build();
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
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
            if (userRepository.selectCount(emailWrapper) > 0) {
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
}

package com.dataplatform.user.controller;

import com.dataplatform.user.dto.*;
import com.dataplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserProfileResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserProfileResponse.builder()
                .id(userService.register(request).getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return userService.getProfile(userId);
    }

    @PutMapping("/profile")
    public UserProfileResponse updateProfile(HttpServletRequest request,
                                              @Valid @RequestBody UpdateProfileRequest updateRequest) {
        Long userId = extractUserId(request);
        return userService.updateProfile(userId, updateRequest);
    }

    @PostMapping("/password")
    public void changePassword(HttpServletRequest request,
                               @Valid @RequestBody PasswordChangeRequest passwordRequest) {
        Long userId = extractUserId(request);
        userService.changePassword(userId, passwordRequest);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        return userService.refreshToken(refreshRequest.getRefreshToken());
    }

    @GetMapping("/actuator/health")
    public String health() {
        return "OK";
    }

    private Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        Long userId = userService.validateToken(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }
        return userId;
    }
}

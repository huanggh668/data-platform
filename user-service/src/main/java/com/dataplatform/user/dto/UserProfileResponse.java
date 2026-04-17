package com.dataplatform.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    /** 角色：admin / user */
    private String role;
    /** 状态：1=启用，0=禁用 */
    private Integer status;
    private LocalDateTime createdAt;
}

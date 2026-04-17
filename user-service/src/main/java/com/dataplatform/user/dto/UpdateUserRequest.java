package com.dataplatform.user.dto;

import lombok.Data;

/**
 * 管理员更新用户请求
 *
 * @author hgh
 * @since 2026-04-17
 */
@Data
public class UpdateUserRequest {

    private String email;

    private String phone;

    /** 状态：1=启用，0=禁用 */
    private Integer status;

    /** 角色：admin / user */
    private String role;
}

package com.brightminds.education.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户登录请求模板（该请求可以直接发送）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiUserRequest {
    /**
     * 用户账号
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
}

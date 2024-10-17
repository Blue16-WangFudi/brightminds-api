package com.brightminds.education.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询用户详细信息的请求模板（需要结合SecurityRequest使用，以便进行权限验证）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiUserInfoRequest {
    /**
     * 用户账号名称.
     */
    private String username;
}

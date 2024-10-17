package com.brightminds.education.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户退出的请求模板（该请求可以直接发送）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiUserExitRequest {
    /**
     * 用户唯一标识符.
     */
    private String id;
    /**
     * 用户的账号名.
     */
    private String username;
}

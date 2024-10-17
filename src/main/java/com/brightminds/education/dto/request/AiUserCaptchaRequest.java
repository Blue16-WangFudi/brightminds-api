package com.brightminds.education.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户短信验证码登录请求体。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiUserCaptchaRequest {
    /**
     * 防盗刷机制：请将phoneNumber异或65536然后提交到该字段
     */
    private String token;
    /**
     * 需要发送的电话号码
     */
    private String phoneNum;
}

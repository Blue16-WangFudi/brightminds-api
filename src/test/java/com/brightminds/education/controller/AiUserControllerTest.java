package com.brightminds.education.controller;


import com.brightminds.education.config.ResultCode;
import com.brightminds.education.dto.ResultResponse;
import com.brightminds.education.dto.request.AiUserCaptchaRequest;
import com.brightminds.education.dto.request.AiUserRequest;
import com.brightminds.education.service.AiUserService;
import com.brightminds.education.service.LoginStateService;
import com.brightminds.education.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 单元测试类，用于测试AiUserController的各类接口请求与响应行为。
 * 该类利用SpringBoot的集成测试框架，结合MockMvc进行模拟HTTP请求的测试，并使用JUnit提供的断言工具进行结果验证。
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc //启用MockMvc
public class AiUserControllerTest {
    private static final Logger log = LoggerFactory.getLogger(AiUserControllerTest.class);
    @Autowired
    private MockMvc mockMvc; // 用于模拟HTTP请求的核心类

    @Autowired
    private ObjectMapper objectMapper; // 用于对象与JSON之间的转换

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LoginStateService loginStateService;

    /**
     * 执行HTTP POST请求，并返回响应内容。
     *
     * @param location 请求的URL路径
     * @param jsonRequest 请求体对象，使用SecurityRequest包装
     * @return 请求响应的内容字符串
     * @throws Exception 请求异常
     */
    public String doPost(String location, String jsonRequest) throws Exception {
        //String jsonRequest = objectMapper.writeValueAsString(securityRequest); // 将请求对象转换为 JSON 字符串
        MvcResult mvcResult = mockMvc.perform(post(location) // 使用 MockMvc 发送 POST 请求
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn(); // 期望返回状态码为 200 OK
        return mvcResult.getResponse().getContentAsString(); // 返回响应内容
    }

    /**
     * 测试验证码发送+登录
     */
    @Test
    public void testSendCaptcha() throws Exception {
        String phoneNum = "15281991073";
        AiUserCaptchaRequest aiUserCaptchaRequest
                = new AiUserCaptchaRequest(String.valueOf(Long.parseLong(phoneNum)^65536),phoneNum);
        String jsonResponse = doPost("/api/v1/user/captcha", objectMapper.writeValueAsString(aiUserCaptchaRequest));
        ResultResponse<?> resultResponse = objectMapper.readValue(jsonResponse, ResultResponse.class);
        // 测试验证码是否发送成功
        Assertions.assertEquals(ResultCode.SUCCESS,resultResponse.getCode(),"测试失败：验证码未发送");
    }

    /**
     * 测试登录接口
     */
    @Test
    public void testAuthenticate() throws Exception {
        AiUserRequest aiUserRequest = new AiUserRequest("username","password");
        String jsonResponse = doPost("/api/v1/user/authenticate", objectMapper.writeValueAsString(aiUserRequest));
        ResultResponse<?> userInfoResponse = objectMapper.readValue(jsonResponse, ResultResponse.class);
        Assertions.assertEquals(ResultCode.SUCCESS,userInfoResponse.getCode(),"测试失败：登录状态码错误");
    }




}

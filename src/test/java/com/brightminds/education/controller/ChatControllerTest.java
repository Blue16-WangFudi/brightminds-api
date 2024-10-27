package com.brightminds.education.controller;


import com.brightminds.education.dto.ResultResponse;
import com.brightminds.education.dto.SecurityRequest;
import com.brightminds.education.dto.request.ChatMessageRequest;
import com.brightminds.education.model.AiUser;
import com.brightminds.education.model.document.ChatMessage;
import com.brightminds.education.model.document.ChatSession;
import com.brightminds.education.repository.AiUserRepository;
import com.brightminds.education.repository.ChatSessionRepository;
import com.brightminds.education.service.AiUserService;
import com.brightminds.education.service.ChatSessionService;
import com.brightminds.education.service.LoginStateService;
import com.brightminds.education.service.SecurityService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc //启用MockMvc
@SpringBootTest
public class ChatControllerTest {
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

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private AiUserRepository aiUserRepository;

    /**
     * 执行HTTP POST请求，并返回响应内容。
     *
     * @param location 请求的URL路径
     * @param securityRequest 请求体对象，使用SecurityRequest包装
     * @return 请求响应的内容字符串
     * @throws Exception 请求异常
     */
    public String doPost(String location, SecurityRequest<?> securityRequest) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(securityRequest); // 将请求对象转换为 JSON 字符串
        MvcResult mvcResult = mockMvc.perform(post(location) // 使用 MockMvc 发送 POST 请求
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn(); // 期望返回状态码为 200 OK
        return mvcResult.getResponse().getContentAsString(); // 返回响应内容
    }

    // 初始化测试数据
    public void init(){
        // 创建一个临时特权账户
        AiUser newAiUser = new AiUser();
        newAiUser.setId("privilege");
        newAiUser.setUsername("admin");
        newAiUser.setPassword("admin");
        newAiUser.setAccessLevel(100);
        aiUserRepository.save(newAiUser);

        // 创建第一个会话
        ChatSession chatSession = new ChatSession();
        chatSession.setSessionId("testSessionID1");
        chatSession.setUserId("admin");
        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage1 = new ChatMessage("system","You are a helpful assistant.");
        ChatMessage chatMessage2 = new ChatMessage("user","Hello!");
        chatMessages.add(chatMessage1);
        chatMessages.add(chatMessage2);
        chatSession.setMessages(chatMessages);
        chatSessionRepository.save(chatSession);

        // 创建第二个会话
        ChatSession chatSession2 = new ChatSession();
        chatSession2.setSessionId("testSessionID2");
        chatSession2.setUserId("admin");
        List<ChatMessage> chatMessages2 = new ArrayList<>();
        ChatMessage chatMessage21 = new ChatMessage("user","Hello!");
        ChatMessage chatMessage22 = new ChatMessage("assistant","Hello how can I help you?");
        chatMessages2.add(chatMessage21);
        chatMessages2.add(chatMessage22);
        chatSession.setMessages(chatMessages2);
        chatSessionRepository.save(chatSession2);

    }

    @Test
    public void getMessageTest() throws Exception {
        init();
        SecurityRequest<ChatMessageRequest> securityRequest = new SecurityRequest<>("privilege",new ChatMessageRequest("testSessionID1"));
        String jsonResponse = doPost("/api/v1/chat/message", securityRequest);
        ResultResponse<?> userInfoResponse = objectMapper.readValue(jsonResponse, ResultResponse.class);
        ChatSession chatSession = (ChatSession) userInfoResponse.getData();
        Assertions.assertEquals(2,chatSession.getMessages().size(),"消息列表数量不对等");

    }

}

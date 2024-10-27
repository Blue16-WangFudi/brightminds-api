package com.brightminds.education.service;


import com.brightminds.education.model.document.ChatMessage;
import com.brightminds.education.model.document.ChatSession;
import com.brightminds.education.repository.ChatSessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
public class ChatSessionServiceTest {
    @Autowired
    ChatSessionService chatSessionService;

    @Autowired
    ChatSessionRepository chatSessionRepository;

    public void init(String tempId){
        ChatMessage msg_system = new ChatMessage("system","You are a helpful assistant.");
        ChatMessage msg_user = new ChatMessage("user","你说谁呀");
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(msg_system);
        messages.add(msg_user);
        //chatSessionService.addMessage(tempId,"testUser",messages);
    }
    @Test
    public void addMessageTest_New(){
        // 添加一条新的ChatSession
        String tempId = UUID.randomUUID().toString();
        init(tempId);

        // 添加完成了，检查添加的记录是否正确
        Optional<ChatSession> bySessionId = chatSessionRepository.findBySessionId(tempId);
        Assertions.assertFalse(bySessionId.isEmpty(),"无法找到新增记录");
    }

    @Test
    public void addMessageTest_Add(){
        // 添加一条新的ChatSession
        String tempId = UUID.randomUUID().toString();
        init(tempId);

        // 在原始记录上继续添加
        ChatMessage msg_assistant = new ChatMessage("assistant","我是小明");
        ChatMessage msg_user2 = new ChatMessage("user","你多少岁了呀");
        List<ChatMessage> messages_add = new ArrayList<>();
        messages_add.add(msg_assistant);
        messages_add.add(msg_user2);
        //chatSessionService.addMessage(tempId,"testUser",messages_add);

        // 添加完成了，检查添加的记录是否正确(通过messages的数量进行检查)
        Optional<ChatSession> bySessionId = chatSessionRepository.findBySessionId(tempId);
        Assertions.assertFalse(bySessionId.isEmpty(),"无法找到新增记录");
        List<ChatMessage> modified = bySessionId.get().getMessages();
        Assertions.assertEquals(4,modified.size(),"最终messages数量不正确");
    }


    @Test
    public void getMessagesTest_Null(){
        // 获取空消息列表
        String tempId = UUID.randomUUID().toString();
        Optional<ChatSession> chatSession = chatSessionService.getMessage(tempId);
        Assertions.assertTrue(chatSession.isPresent(),"返回了空指针");
    }

    @Test
    public void getMessagesTest_Exist(){
        // 添加一条新的ChatSession
        String tempId = UUID.randomUUID().toString();
        init(tempId);
        Optional<ChatSession> chatSession = chatSessionService.getMessage(tempId);

        Assertions.assertTrue(chatSession.isPresent(),"返回了空指针");
        List<ChatMessage> messages = chatSession.get().getMessages();
        Assertions.assertEquals(2,messages.size(),"返回数量不正确");
    }

    @Test
    public void getSessionTest(){
        init(UUID.randomUUID().toString());
        init(UUID.randomUUID().toString());
        List<ChatSession> byUserId = chatSessionRepository.findByUserId("testUser");
        Assertions.assertEquals(2,byUserId.size(),"查找到指定用户的会话数量不正确");

    }
}

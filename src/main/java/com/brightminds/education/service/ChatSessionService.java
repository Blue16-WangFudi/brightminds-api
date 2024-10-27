package com.brightminds.education.service;


import com.brightminds.education.model.document.ChatMessage;
import com.brightminds.education.model.document.ChatSession;
import com.brightminds.education.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatSessionService {
    @Autowired
    ChatSessionRepository chatSessionRepository;
    public ChatSession saveSession(String sessionId, String username, List<ChatMessage> chatMessage){
        ChatSession chatSession = new ChatSession(sessionId, username, chatMessage);
        return chatSessionRepository.save(chatSession);
    }
    public Optional<ChatSession> getMessage(String sessionId){
        return chatSessionRepository.findBySessionId(sessionId);
    }

    public List<ChatSession> getSession(String userId){
        return chatSessionRepository.findByUserId(userId);

    }
}

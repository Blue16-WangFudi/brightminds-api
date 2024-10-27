package com.brightminds.education.repository;
import com.brightminds.education.model.document.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


// TODO 注意每一个ChatSession的ID项也得指定成chatSession（检查一下不然会重复）
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    Optional<ChatSession> findBySessionId(String sessionId);
    List<ChatSession> findByUserId(String userId);
}
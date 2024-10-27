package com.brightminds.education.model.document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "Chat_Session")
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @Id
    private String sessionId;
    private String userId;
    private List<ChatMessage> messages;

}


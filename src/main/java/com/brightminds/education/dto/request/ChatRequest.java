package com.brightminds.education.dto.request;


import com.brightminds.education.model.document.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    private String sessionId;
    private List<ChatMessage> messages;
    private Map<String,Object> parameters;
}

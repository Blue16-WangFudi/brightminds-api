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
    private List<String> tools; // TODO 需要使用的工具名称列表，现在暂时只支持name不支持category，后续实现
    private Map<String,Object> parameters;
}

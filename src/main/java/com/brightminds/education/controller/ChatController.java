package com.brightminds.education.controller;


import com.brightminds.education.config.ResultCode;
import com.brightminds.education.dto.ResultResponse;
import com.brightminds.education.dto.SecurityRequest;
import com.brightminds.education.dto.request.ChatMessageRequest;
import com.brightminds.education.dto.request.ChatRequest;
import com.brightminds.education.dto.request.ChatSessionRequest;
import com.brightminds.education.model.AiUser;
import com.brightminds.education.model.document.ChatMessage;
import com.brightminds.education.model.document.ChatSession;
import com.brightminds.education.service.AiUserService;
import com.brightminds.education.service.ChatSessionService;
import com.brightminds.education.service.SecurityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AiUserService aiUserService;

    @Value("${DASHSCOPE_API_KEY}")
    private String DASHSCOPE_API_KEY;

    private final WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController() {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(30));
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl("https://dashscope.aliyuncs.com")
                .build();
    }

    /**
     * 请求大模型的调用，支持SSE调用方式
     * @param securityRequest 安全请求封装类，提交一个ChatRequest对象{@link ChatRequest}
     *                        注意，sessionId可以选择不提交，不提交默认随机生成一个sessionId，如果使用和已有对话相同的sessionId，则会获取之前
     *                        的对话内容进行进一步对话。
     * @return 包含登录结果的响应体
     */
    @PostMapping("/chat-stream")
    public SseEmitter streamChat(@RequestBody SecurityRequest<ChatRequest> securityRequest) throws JsonProcessingException {
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "/chat-stream")) {
            //return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
            return null; //这里还不清楚怎么处理，因为是SSE所以不太好弄
        }
        // 尝试从数据库获取消息
        Optional<ChatSession> chatSession = chatSessionService.getMessage(securityRequest.getData().getSessionId());

        List<ChatMessage> recordMsg; // 最终传递给大模型的消息汇总
        if(chatSession.isPresent()){
            // 有历史记录则合并
            recordMsg = chatSession.get().getMessages();
            List<ChatMessage> requestMsg = securityRequest.getData().getMessages();
            recordMsg.addAll(requestMsg);
        }else
            // 没有历史记录，直接使用用户提交的对话请求
            recordMsg = securityRequest.getData().getMessages();

        // 构建一个对话请求
        Map<String,Object> qwenChatRequest = new HashMap<>();
        qwenChatRequest.put("messages",recordMsg); // 置入消息（Message）
        qwenChatRequest.putAll(securityRequest.getData().getParameters());// 置入参数（Parameter）

        SseEmitter emitter = new SseEmitter();
        StringBuilder fullResponse = new StringBuilder(); // 获取流式请求的完整文本，用于添加到数据库中
        boolean finalHasHistory = chatSession.isPresent();
        webClient.post()
                .uri("/compatible-mode/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + DASHSCOPE_API_KEY)
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(qwenChatRequest)))
                .retrieve()
                .bodyToFlux(String.class) // Stream response as String
                .subscribe(
                        chunk -> {
                            try {
                                // 获取局部内容并拼接
                                JsonNode jsonNode = objectMapper.readTree(chunk);
                                JsonNode choices = jsonNode.get("choices");

                                if (choices != null && choices.isArray()) {
                                    for (JsonNode choice : choices) {
                                        JsonNode delta = choice.get("delta");
                                        if (delta != null && delta.has("content")) {
                                            String content = delta.get("content").asText();
                                            if(!content.equals("null"))
                                                fullResponse.append(content); // Accumulate the content
                                        }
                                    }
                                }

                                // Send each chunk to the frontend as SSE event
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        emitter::completeWithError,
                        () -> {
                            // 这里是发送完成后的处理事件
                            recordMsg.add(new ChatMessage("assistant",fullResponse.toString()));
                            // 获取用户账号名称
                            Optional<AiUser> byToken = aiUserService.getByToken(securityRequest.getToken());
                            chatSessionService.saveSession(securityRequest.getData().getSessionId(),byToken.get().getUsername(), recordMsg);

                            emitter.complete();
                        }
                );
        return emitter;
    }

    @PostMapping("/message")
    public ResultResponse<?> getMessage(@RequestBody SecurityRequest<ChatMessageRequest> securityRequest){
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "/message")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }

        Optional<ChatSession> chatSession = chatSessionService.getMessage(securityRequest.getData().getSessionId());
        if(chatSession.isEmpty()){
            return new ResultResponse<>(ResultCode.FAILURE,"未查询到当前会话保存的信息",null);
        }
        return new ResultResponse<>(ResultCode.SUCCESS,"查询会话消息成功",chatSession.get());
    }

    @PostMapping("/session")
    public ResultResponse<?> getUserSession(@RequestBody SecurityRequest<ChatSessionRequest> securityRequest){
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "/session")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }
        List<ChatSession> session = chatSessionService.getSession(securityRequest.getData().getUserId());
        if(session.isEmpty()){
            return new ResultResponse<>(ResultCode.FAILURE,"未查询到当前用户的所有会话信息",null);
        }

        List<String> sessionIds = new ArrayList<>();
        for(ChatSession chatSession : session){
            sessionIds.add(chatSession.getSessionId());
        }

        return new ResultResponse<>(ResultCode.SUCCESS,"查询成功",sessionIds);

    }
}

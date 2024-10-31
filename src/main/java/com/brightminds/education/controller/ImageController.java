package com.brightminds.education.controller;

import com.brightminds.education.config.ResultCode;
import com.brightminds.education.dto.ResultResponse;
import com.brightminds.education.dto.SecurityRequest;
import com.brightminds.education.dto.qwen.QwenImageStatusResponse;
import com.brightminds.education.dto.qwen.QwenImageTaskResponse;
import com.brightminds.education.dto.request.ImageStatusRequest;
import com.brightminds.education.dto.request.ImageTaskRequest;
import com.brightminds.education.service.AiUserService;
import com.brightminds.education.service.SecurityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private AiUserService aiUserService;

    @Value("${DASHSCOPE_API_KEY}")
    private String DASHSCOPE_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/generate")
    public ResultResponse<?> generateImage(@RequestBody SecurityRequest<ImageTaskRequest> securityRequest) throws JsonProcessingException {

        // 权限校验
        if (!securityService.checkAccess(securityRequest, "brightminds.imagecontroller.generate")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }

        ImageTaskRequest imageTaskRequest = securityRequest.getData();
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-DashScope-Async", "enable");
        headers.set("Authorization", "Bearer " + DASHSCOPE_API_KEY);

        // 设置请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "wanx-v1");

        // 在请求体中添加提示词
        ImageTaskRequest.ImageInput prompt = imageTaskRequest.getInput();
        Map<String, String> input = new HashMap<>();
        input.put("prompt", prompt.getPrompt());
        requestBody.put("input", input);

        // 在请求体中添加额外参数
        requestBody.put("parameters", imageTaskRequest.getParameters());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis";

        String result = restTemplate.postForObject(url, entity, String.class);

        try{
            QwenImageTaskResponse qwenImageTaskResponse = objectMapper.readValue(result, QwenImageTaskResponse.class);
            return new ResultResponse<>(ResultCode.SUCCESS, "创建任务成功", qwenImageTaskResponse);
        }
        catch(Exception e){
            return new ResultResponse<>(ResultCode.BAD_REQUEST, "获取响应体失败", objectMapper.readTree(result));
        }


    }

    @PostMapping("/status")
    public ResultResponse<?> getTaskStatus(@RequestBody SecurityRequest<ImageStatusRequest> securityRequest) throws JsonProcessingException {

        // 权限校验
        if (!securityService.checkAccess(securityRequest, "brightminds.imagecontroller.status")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }

        ImageStatusRequest imageStatusRequest = securityRequest.getData();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + DASHSCOPE_API_KEY);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("taskId", imageStatusRequest.getTaskId());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        String url = "https://dashscope.aliyuncs.com/api/v1/tasks/" + imageStatusRequest.getTaskId();

        String result = restTemplate.postForObject(url, entity, String.class);

        try{
            QwenImageStatusResponse qwenImageStatusResponse = objectMapper.readValue(result, QwenImageStatusResponse.class);
            return new ResultResponse<>(ResultCode.SUCCESS, "创建任务成功", qwenImageStatusResponse);
        }
        catch(Exception e){
            return new ResultResponse<>(ResultCode.BAD_REQUEST, "获取响应体失败", objectMapper.readTree(result));
        }

    }



}

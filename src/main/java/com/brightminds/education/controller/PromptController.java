package com.brightminds.education.controller;


import com.brightminds.education.config.ResultCode;
import com.brightminds.education.dto.ResultResponse;
import com.brightminds.education.dto.SecurityRequest;
import com.brightminds.education.dto.request.SystemPromptRequest;
import com.brightminds.education.service.SecurityService;
import com.brightminds.education.service.SystemPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat/prompt")
public class PromptController {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private SystemPromptService systemPromptService;

    @PostMapping("/get")
    public ResultResponse<?> getSystemPrompt(@RequestBody SecurityRequest<SystemPromptRequest> securityRequest){
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "brightminds.promptcontroller.get")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }
        SystemPromptRequest systemPromptRequest = securityRequest.getData();
        String systemPromptByName = systemPromptService.getSystemPromptByName(systemPromptRequest.getName(), systemPromptRequest.getParameters());
        return new ResultResponse<>(ResultCode.SUCCESS, "Prompt生成成功", systemPromptByName);
    }
}

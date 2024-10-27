package com.brightminds.education.dto.qwen;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QwenImageTaskResponse {
    private Output output;
    @JsonProperty("request_id")
    private String requestId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output {
        @JsonProperty("task_id")
        private String taskId;
        @JsonProperty("task_status")
        private String taskStatus;

    }
}


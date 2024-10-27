package com.brightminds.education.dto.qwen;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QwenImageStatusResponse {
    @JsonProperty("request_id")
    private String requestId;
    private Output output;
    private Usage usage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {
        @JsonProperty("task_id")
        private String taskId;
        @JsonProperty("task_status")
        private String taskStatus;
        @JsonProperty("submit_time")
        private String submitTime;
        @JsonProperty("scheduled_time")
        private String scheduledTime;
        @JsonProperty("end_time")
        private String endTime;
        private List<Result> results;
        @JsonProperty("task_metrics")
        private TaskMetrics taskMetrics;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Result {
            private String url;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TaskMetrics {
            @JsonProperty("TOTAL")
            private int TOTAL;
            @JsonProperty("SUCCEEDED")
            private int SUCCEEDED;
            @JsonProperty("FAILED")
            private int FAILED;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        @JsonProperty("image_count")
        private int imageCount;
    }
}


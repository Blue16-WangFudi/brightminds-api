package com.brightminds.education.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageTaskRequest {
    private String sessionId;
    private ImageInput input;
    private Map<String,Object> parameters;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageInput{
        private String prompt;
    }
}

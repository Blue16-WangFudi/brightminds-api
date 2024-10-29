package com.brightminds.education.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemPromptRequest {
    private String name;
    private String category;
    private Map<String,String> parameters;
}

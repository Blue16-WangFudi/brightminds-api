package com.brightminds.education.service;

import com.brightminds.education.model.document.FunctionCall;
import com.brightminds.education.repository.FunctionCallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FunctionCallService {
    @Autowired
    FunctionCallRepository functionCallRepository;
    public Map<String, Object> getToolByName(String name){
        return functionCallRepository.findByName(name).getData();
    }

    public List<Map<String, Object>> getToolsByCategory(String category){
        List<FunctionCall> byCategory = functionCallRepository.findByCategory(category);
        List<Map<String, Object>> tools = new ArrayList<>();
        for (FunctionCall fc : byCategory) {
            tools.add(fc.getData());
        }
        return tools;
    }
}

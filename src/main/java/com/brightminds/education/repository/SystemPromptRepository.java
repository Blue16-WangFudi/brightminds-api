package com.brightminds.education.repository;

import com.brightminds.education.model.document.SystemPrompt;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;


// TODO 注意每一个ChatSession的ID项也得指定成chatSession（检查一下不然会重复）
public interface SystemPromptRepository extends MongoRepository<SystemPrompt, String> {
    SystemPrompt findByName(String name);
    List<SystemPrompt> findByCategory(String category);
}
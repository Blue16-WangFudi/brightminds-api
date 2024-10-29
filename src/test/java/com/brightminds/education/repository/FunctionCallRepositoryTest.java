package com.brightminds.education.repository;

import com.brightminds.education.model.document.FunctionCall;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FunctionCallRepositoryTest {

    @Autowired
    FunctionCallRepository functionCallRepository;

    @Test
    public void getByNameTest(){
        FunctionCall byName = functionCallRepository.findByName("generatePic");

    }
}

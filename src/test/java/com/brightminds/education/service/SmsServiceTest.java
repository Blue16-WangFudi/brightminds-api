package com.brightminds.education.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmsServiceTest {
    @Autowired
    private SmsService smsService;

    @Test
    public void sendSmsTest(){
        smsService.sendCaptcha("+8615281991073","9999");
    }
}

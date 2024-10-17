package com.brightminds.education.service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class SecurityServiceTest {
    @Autowired
    SecurityService securityService;



    @Test
    public void XORtest() {

        long timestamp = (int) (System.currentTimeMillis() / 1000 / 60);

        System.out.println(timestamp);

        String originalText = "Hello, World!";
        System.out.println("Original Text: " + originalText);

        // 获取当前时间戳（精确到分钟）
        long timestamp_2 = new Date().getTime() / (1000 * 60);

        // 加密
        String encryptedText = SecurityService.encrypt(originalText, timestamp);
        System.out.println("Encrypted Text: " + encryptedText);

        // 解密
        String decryptedText = SecurityService.decrypt(encryptedText, timestamp_2);
        System.out.println("Decrypted Text: " + decryptedText);
    }

    @Test
    public void XORSimpletest() {
        String phoneNum = "15281991073";
        long phoneNumInt = Long.parseLong(phoneNum);
        long xor = phoneNumInt^65536;
        System.out.println(xor);
        long xorInt = xor^65536;
        System.out.println(xorInt);
    }


}

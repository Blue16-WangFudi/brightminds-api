package com.brightminds.education.service;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginStateService {
    private static final Map<String,String> loginState= new ConcurrentHashMap<>();//存放登录状态

    public void setLoginState(String phoneNumber, String captcha) {
        loginState.put(phoneNumber,captcha);
    }

    public boolean isLogin(String phoneNumber,String captcha) {
        if(loginState.containsKey(phoneNumber)) {
            String correctCaptcha = loginState.get(phoneNumber);
            loginState.remove(phoneNumber); //删除对应的验证状态（因为验证通过了这个验证码就失效了）
            return correctCaptcha.equals(captcha);
        }
        return false;
    }
}

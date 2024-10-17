package com.brightminds.education.controller;

import com.brightminds.education.config.ResultCode;
import com.brightminds.education.dto.ResultResponse;
import com.brightminds.education.dto.SecurityRequest;
import com.brightminds.education.dto.request.AiUserCaptchaRequest;
import com.brightminds.education.dto.request.AiUserExitRequest;
import com.brightminds.education.dto.request.AiUserRequest;
import com.brightminds.education.model.AiUser;
import com.brightminds.education.service.AiUserService;
import com.brightminds.education.service.LoginStateService;
import com.brightminds.education.service.SecurityService;
import com.brightminds.education.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Random;
import java.util.UUID;

/**
 * @ClassName: AiUserController
 * @Description: 控制器类，用于处理用户登录注册和信息保存的相关操作。
 * @Author: Blue16
 * @Date: 2024-10-05
 */
@RestController
@RequestMapping("/api/v1/user")
public class AiUserController {

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private LoginStateService loginStateService;

    /**
     * 账号登录。未注册账号会自动注册。如果登录成功，状态码为200，登录成功并且注册，状态码为201，其余为登录失败（400）
     *
     * @param aiUserRequest 包含用户名和密码(或者收到的手机验证码)的请求体
     * @return 包含登录结果的响应体
     */
    @PostMapping("/authenticate")
    @Transactional // 确保登录操作中数据库的原子性
    public ResultResponse<?> aiUserLogin(@RequestBody AiUserRequest aiUserRequest) {
        try {
            // 从数据库获取用户信息
            AiUser existingUser = aiUserService.get(aiUserRequest.getUsername());
            // 如果未找到用户，返回未注册
            if(existingUser==null){
                // 创建一个空用户
                AiUser aiUser = new AiUser(UUID.randomUUID().toString(), aiUserRequest.getUsername(), UUID.randomUUID().toString(), "", "", "", "", "", 1);
                aiUserService.update(aiUser);
                aiUser.setPassword(null);
                return new ResultResponse<>(ResultCode.SUCCESS_PUT, "登录成功，已添加新用户信息", aiUser);
            }
            // 如果用户存在且密码匹配(也可能是验证码匹配)，直接登录
            if (existingUser.getPassword().equals(aiUserRequest.getPassword()) || loginStateService.isLogin(aiUserRequest.getUsername(), aiUserRequest.getPassword())) {
                AiUser updatedUser = securityService.changeUserToken(existingUser);
                updatedUser.setPassword(null); // 隐藏密码
                return new ResultResponse<>(ResultCode.SUCCESS, "登录成功", updatedUser);
            }else
                return new ResultResponse<>(ResultCode.FAILURE, "登录失败，账号密码错误", null);

        } catch (Exception e) {
            // 捕获所有异常，并返回详细错误信息
            return new ResultResponse<>(ResultCode.FAILURE, "登录失败，账号密码错误或者服务器出现问题", null);
        }
    }




    /**
     * 创建一个手机号登录请求，并在服务器生成与手机号对应的验证码，并将验证码发送到对应手机
     * @param aiUserCaptchaRequest 包含验证token和手机号的请求体.token请提供电话号码与65536异或运算的结果：phoneNum^65536
     * @return 包含登录结果的响应体
     */
    @PostMapping("/captcha")
    public ResultResponse<?> captchaLogin(@RequestBody AiUserCaptchaRequest aiUserCaptchaRequest) {
        boolean debug = false;
        if(!securityService.checkAccessByPhoneNum(aiUserCaptchaRequest.getToken(),aiUserCaptchaRequest.getPhoneNum())){
            if(!debug){
                return new ResultResponse<>(ResultCode.BAD_REQUEST, "验证码发送失败，无权访问该接口", null);
            }
        }
        Random random = new Random();
        int captcha = 100000 + random.nextInt(900000); // 生成100000到999999之间的随机数
        smsService.sendCaptcha(aiUserCaptchaRequest.getPhoneNum(), String.valueOf(captcha));
        loginStateService.setLoginState(aiUserCaptchaRequest.getPhoneNum(), String.valueOf(captcha));
        return new ResultResponse<>(ResultCode.SUCCESS, "验证码发送成功", null);
    }

    /**
     * 修改用户信息。注意：提交完整对象时，空值字段会覆盖现有值。同时必须已经注册过后的账号才能对其进行修改。
     *
     * @param securityRequest 安全请求封装类(AiUser)，包含用户信息和认证信息。注意，id和access_level不支持修改，密码如果不提供则使用数据库中的记录值
     * @return 返回更新后的用户信息或错误提示。
     */
    @PostMapping("/updateInfo")
    public ResultResponse<?> updateUserInfo(@RequestBody SecurityRequest<AiUser> securityRequest) {
        if (!securityService.checkAccess(securityRequest, "/updateInfo")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }

        AiUser aiUser = aiUserService.get(securityRequest.getData().getUsername());
        if (aiUser == null) {
            return new ResultResponse<>(ResultCode.NOT_FOUND, "该用户不存在", null);
        }

        AiUser request = securityRequest.getData();
        // 这两个字段是默认不能修改的，置为注册的值
        request.setId(aiUser.getId());
        request.setAccessLevel(aiUser.getAccessLevel());
        if(request.getPassword()==null)
            request.setPassword(aiUser.getPassword());
        aiUserService.update(request);
        return new ResultResponse<>(ResultCode.SUCCESS, "操作成功", request);
    }


    /**
     * 账号退出登录。
     *
     * @param aiUserExitRequest 退出请求体，包含用户名和用户ID
     * @return 退出结果的响应体
     */
    @PostMapping("/logout")
    public ResultResponse<?> aiUserExit(@RequestBody AiUserExitRequest aiUserExitRequest) {
        try {
            // 检查用户信息是否匹配
            AiUser aiUser = aiUserService.get(aiUserExitRequest.getUsername());
            if (aiUser != null && aiUserExitRequest.getId().equals(aiUser.getId())) {
                securityService.changeUserToken(aiUser); // 更改用户的安全令牌
                return new ResultResponse<>(200, "退出登录成功", null);
            } else {
                return new ResultResponse<>(300, "用户信息不匹配，退出失败。", null);
            }
        } catch (Exception e) {
            return new ResultResponse<>(300, "获取登录信息错误，退出失败。", null);
        }
    }
}



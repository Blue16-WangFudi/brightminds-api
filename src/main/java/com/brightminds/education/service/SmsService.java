package com.brightminds.education.service;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private SmsClient client;

    @Value("${tencentcloudapi.sms.secretId}")
    private String secretId;

    @Value("${tencentcloudapi.sms.secretKey}")
    private String secretKey;

    @Value("${tencentcloudapi.sms.region}")
    private String region;

    @Value("${tencentcloudapi.sms.smsSdkAppId}")
    private String smsSdkAppId;

    @Value("${tencentcloudapi.sms.templateId}")
    private String templateId;

    @Value("${tencentcloudapi.sms.signName}")
    private String signName;

    @PostConstruct
    public void init(){
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
//        secretId = System.getenv(secretId);
//        secretKey = System.getenv(secretKey);
        signName="blue16Cn网";
        Credential cred = new Credential(secretId, secretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        client = new SmsClient(cred, region, clientProfile);
    }

    public void sendCaptcha(String phoneNumber, String captcha) {

        try{
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {phoneNumber};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId(smsSdkAppId);
            req.setTemplateId(templateId);
            req.setSignName(signName);

            String[] templateParamSet1 = {captcha};
            req.setTemplateParamSet(templateParamSet1);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e);
        }
    }
}

package com.brightminds.education.service;
import com.brightminds.education.dto.SecurityRequest;
import com.brightminds.education.model.AccessLevel;
import com.brightminds.education.model.AiUser;
import com.brightminds.education.repository.AccessLevelRepository;
import com.brightminds.education.repository.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

/**
 * 安全服务类，主要用于处理权限判定、用户访问等级查询及Token更新等安全相关的业务逻辑。
 */
@Service
public class SecurityService {

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private AccessLevelRepository accessLevelRepository;

    @Autowired
    private AiUserRepository aiUserRepository;

    /**
     * 更新用户的Token，采用删除原来的再新建一个新的Token的方式。
     *
     * @param aiUser 要更新Token的用户信息对象
     * @return 更新后的用户信息 {@link AiUser}
     */
    public AiUser changeUserToken(AiUser aiUser) {
        AiUser newAiUser = new AiUser(
                UUID.randomUUID().toString(),
                aiUser.getUsername(),
                aiUser.getPassword(),
                aiUser.getName(),
                aiUser.getGender(),
                aiUser.getName(),
                aiUser.getHobby(),
                aiUser.getInformation(),
                aiUser.getAccessLevel()
        );
        // 删除原有用户记录
        aiUserService.delete(aiUser.getUsername());
        // 保存更新后的用户信息
        aiUserService.update(newAiUser);
        return newAiUser;
    }

    /**
     * 检查指定的请求是否有权限访问某个接口。
     *
     * @param securityRequest 请求的安全信息
     * @param location 接口的路径或位置
     * @return 如果请求的用户权限等级大于等于接口访问等级，返回 true，否则返回 false。
     */
    public boolean checkAccess(SecurityRequest<?> securityRequest, String location) {
        AccessLevel defaultAccessLevel = new AccessLevel();
        defaultAccessLevel.setAccessLevel(100); // 默认接口访问权限

        // 获取指定接口的访问等级，如果不存在则使用默认值
        Optional<AccessLevel> accessLevelByLocation = accessLevelRepository.findAccessLevelByLocation(location);
        int requiredAccessLevel = accessLevelByLocation.orElse(defaultAccessLevel).getAccessLevel();

        return getAccessLevel(securityRequest.getToken()) >= requiredAccessLevel;
    }

    /**
     * 根据Token获取用户的访问权限等级。
     *
     * @param token 用户的身份标识Token
     * @return 用户的访问权限等级
     */
    public int getAccessLevel(String token) {
        AiUser defaultAiUser = new AiUser();
        defaultAiUser.setAccessLevel(0); // 默认用户访问权限等级

        // 根据Token查询用户访问权限，如果查询不到则返回默认等级
        Optional<AiUser> userOptional = aiUserRepository.findById(token);
        return userOptional.orElse(defaultAiUser).getAccessLevel();
    }

    // 检查访问权限
    public boolean checkAccessByPhoneNum(String token,String phoneNum) {
        Long value =  Long.valueOf(token);
        Long parsedValue = value^65536;
        return parsedValue.equals(Long.parseLong(phoneNum));
    }


    public static String encrypt(String text, long key) {
        return xorEncryptDecrypt(text, key);
    }

    public static String decrypt(String text, long key) {
        return xorEncryptDecrypt(text, key);
    }

    private static String xorEncryptDecrypt(String input, long key) {
        StringBuilder output = new StringBuilder();
        String keyStr = Long.toString(key);

        for (int i = 0; i < input.length(); i++) {
            // 取得密钥的每一个字符，循环使用
            int keyIndex = i % keyStr.length();
            char keyChar = keyStr.charAt(keyIndex);
            // 对输入的每一个字符进行异或运算
            char encryptedChar = (char) (input.charAt(i) ^ keyChar);
            output.append(encryptedChar);
        }

        return output.toString();
    }
}

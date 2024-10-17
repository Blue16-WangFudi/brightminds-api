package com.brightminds.education.service;
import com.brightminds.education.repository.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.brightminds.education.model.AiUser;

/**
 * 账号信息管理服务类，用于处理用户账号的增删查改操作。
 * 包括根据用户名查询、更新账号信息以及删除用户等功能。
 */
@Service
@Transactional
public class AiUserService {

    @Autowired
    private AiUserRepository aiUserRepository;

    /**
     * 根据账号名称查询用户账号信息。
     *
     * @param username 账号名称
     * @return 对应的用户账号信息 {@link AiUser}
     */
    public AiUser get(String username) {
        return aiUserRepository.findByUsername(username);
    }

    /**
     * 添加或更新用户账号信息。
     *
     * @param aiUser 用户账号信息对象
     */
    public void update(AiUser aiUser) {
        aiUserRepository.save(aiUser);
    }

    /**
     * 删除指定用户名的用户账号信息。
     *
     * @param username 用户名
     */
    public void delete(String username) {
        aiUserRepository.deleteByUsername(username);
    }
}

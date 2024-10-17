package com.brightminds.education.repository;
import com.brightminds.education.model.AiUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


/**
 * 用于在AiUser实体上执行数据库操作的接口.
 * 使用Spring Data JPA的JpaRepository提供CRUD操作和自定义查询.
 */
public interface AiUserRepository extends JpaRepository<AiUser, String> {
    AiUser findByUsername(String username);
    void deleteByUsername(String username);
    @NotNull
    Optional<AiUser> findById(@NotNull String id);
}
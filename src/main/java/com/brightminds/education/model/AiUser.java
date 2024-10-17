// AiUser.java
package com.brightminds.education.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户账号的实体类.
 * 包含用户账号密码及访问权限等级信息.
 */
@Entity
@Table(name = "ai_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiUser {
    /**
     * 用户唯一标识符.
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    /**
     * 用户名称, 必须唯一且不可为空.
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /**
     * 用户密码, 不可为空.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 学生姓名.
     */
    @Column(name = "name")
    private String name;

    /**
     * 性别.
     */
    @Column(name = "gender")
    private String gender;

    /**
     * 年龄.
     */
    @Column(name = "age")
    private String age;

    /**
     * 业余爱好.
     */
    @Column(name = "hobby")
    private String hobby;

    /**
     * 附加信息.
     */
    @Column(name = "information")
    private String information;

    /**
     * 用户的访问权限级别，默认为1.
     */
    @Column(name = "access_level", nullable = false)
    private int accessLevel = 1;
}

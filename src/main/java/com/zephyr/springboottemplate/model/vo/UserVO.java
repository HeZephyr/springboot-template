package com.zephyr.springboottemplate.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户视图（脱敏）
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户 id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;
    @Serial
    private static final long serialVersionUID = 1L;
}

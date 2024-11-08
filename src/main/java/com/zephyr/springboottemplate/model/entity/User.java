package com.zephyr.springboottemplate.model.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 */
@Data // 通过lombok插件自动生成getter、setter、toString等方法
public class User implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 开放平台id
     */
    private String unionId;
    /**
     * 公众号openId
     */
    private String mpOpenId;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String UserAvatar;
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
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    /**
     * 序列化id
     */
    @Serial // 标识该字段为序列化版本标识符，用于校验类的兼容性
    private static final long serialVersionUID = 1L;
}

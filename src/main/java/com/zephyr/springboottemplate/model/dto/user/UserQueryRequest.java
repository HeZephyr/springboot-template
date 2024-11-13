package com.zephyr.springboottemplate.model.dto.user;

import com.zephyr.springboottemplate.common.PageRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户查询请求
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 开放平台ID
     */
    private String unionId;

    /**
     * 公众号OpenID
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}

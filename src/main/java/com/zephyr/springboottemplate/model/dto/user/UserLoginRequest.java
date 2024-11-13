package com.zephyr.springboottemplate.model.dto.user;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {
    private String userAccount;
    private String userPassword;

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;
}

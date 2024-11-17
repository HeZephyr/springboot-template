package com.zephyr.springboottemplate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zephyr.springboottemplate.model.dto.user.UserQueryRequest;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.vo.LoginUserVO;
import com.zephyr.springboottemplate.model.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest; // springboot 3.x use jakarta.servlet.http.HttpServletRequest
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.List;

/**
 * 用户服务接口
 * 定义了用户相关的业务操作方法
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码，用于验证两次输入是否一致
     * @return 注册成功后生成的用户 ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request HTTP 请求对象，用于存储会话信息
     * @return 登录成功后的用户信息视图对象
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 微信授权登录
     *
     * @param wxOAuth2UserInfo 微信授权后获取的用户信息
     * @param request HTTP 请求对象，用于存储会话信息
     * @return 登录成功后的用户信息视图对象
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request HTTP 请求对象，用于获取会话中的用户信息
     * @return 当前登录的用户对象，如果未登录则抛出异常
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许空结果）
     *
     * @param request HTTP 请求对象，用于获取会话中的用户信息
     * @return 当前登录的用户对象，如果未登录则返回 null
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 判断当前用户是否为管理员（基于 HTTP 请求）
     *
     * @param request HTTP 请求对象，用于获取会话中的用户信息
     * @return 如果是管理员用户返回 true，否则返回 false
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断指定用户是否为管理员
     *
     * @param user 用户对象
     * @return 如果是管理员用户返回 true，否则返回 false
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request HTTP 请求对象，用于清除会话中的用户信息
     * @return 注销成功返回 true，否则返回 false
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取登录用户的视图对象（脱敏信息）
     *
     * @param user 用户对象
     * @return 登录用户的视图对象
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户的视图对象（脱敏信息）
     *
     * @param user 用户对象
     * @return 用户的视图对象
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户列表的视图对象（脱敏信息）
     *
     * @param userList 用户对象列表
     * @return 用户视图对象列表
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 根据查询请求构建用户查询条件
     *
     * @param userQueryRequest 用户查询请求对象，包含查询条件
     * @return 用户查询条件封装对象
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
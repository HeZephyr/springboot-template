package com.zephyr.springboottemplate.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.constant.SortConstant;
import com.zephyr.springboottemplate.mapper.UserMapper;
import com.zephyr.springboottemplate.model.dto.user.UserQueryRequest;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.enums.UserRoleEnum;
import com.zephyr.springboottemplate.model.vo.LoginUserVO;
import com.zephyr.springboottemplate.model.vo.UserVO;
import com.zephyr.springboottemplate.service.UserService;
import com.zephyr.springboottemplate.utils.SqlUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;

import com.zephyr.springboottemplate.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zephyr.springboottemplate.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 密码加密的盐值
    public static final String SALT = "zephyr";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 参数校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码、确认密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度不能小于 4 位");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于 8 位");
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 同步锁， 防止并发注册同一账号
        synchronized (userAccount.intern()) {
            // 2. 检查账号是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号已存在");
            }

            // 3. 密码加密
            String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());

            // 4. 创建用户并保存信息
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user); // 保存到数据库
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 参数校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码不能为空");
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度不能小于 4 位");
        }

        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于 8 位");
        }

        // 2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());

        // 3. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper); // 查询数据库
        if (user == null) {
            log.info("用户登录失败，账号或密码错误，userAccount: {}", userAccount);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号或密码错误");
        }

        // 4. 保存登录状态到 Session
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 5. 返回登录用户视图对象
        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        // 1. 获取微信用户的 UnionId 和 OpenId
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();

        // 同步锁，防止并发登录或注册同一用户
        synchronized (unionId.intern()) {
            // 2. 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);

            // 3. 如果用户存在且被封禁，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "您的账号已被封禁，禁止登录");
            }

            // 4. 如果用户不存在，创建新用户并保存
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl()); // 设置用户头像
                user.setUserName(wxOAuth2UserInfo.getNickname()); // 设置用户昵称
                boolean saveResult = this.save(user);
                if (!saveResult) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库错误");
                }
            }

            // 5. 保存用户登录状态到 Session
            request.getSession().setAttribute(USER_LOGIN_STATE, user);

            // 6. 返回用户的登录视图对象
            return this.getLoginUserVO(user);
        }
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1. 从 Session 中获取当前用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        // 2. 如果用户未登录，抛出未登录异常
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 3. 验证用户是否存在于数据库
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 4. 返回当前用户信息
        return currentUser;
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 1. 从 Session 中获取当前用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        // 2. 如果用户未登录或 ID 为空，返回 null
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }

        // 3. 返回数据库中的用户信息
        return this.getById(currentUser.getId());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 1. 从 Session 中获取当前用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;

        // 2. 判断用户是否为管理员
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        // 判断指定用户是否为管理员
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 1. 检查用户是否已登录
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            return false;
        }

        // 2. 移除用户登录状态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        // 1. 如果用户为空，返回 null
        if (user == null) {
            return null;
        }

        // 2. 转换为登录用户视图对象
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        // 1. 如果用户为空，返回 null
        if (user == null) {
            return null;
        }

        // 2. 转换为用户视图对象
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        // 1. 如果用户列表为空，返回空列表
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }

        // 2. 转换为用户视图对象列表
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 1. 校验请求参数是否为空
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询请求参数为空");
        }

        // 2. 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userQueryRequest.getId() != null, "id", userQueryRequest.getId());
        queryWrapper.eq(StringUtils.isNotBlank(userQueryRequest.getUnionId()), "unionId", userQueryRequest.getUnionId());
        queryWrapper.eq(StringUtils.isNotBlank(userQueryRequest.getMpOpenId()), "mpOpenId", userQueryRequest.getMpOpenId());
        queryWrapper.like(StringUtils.isNotBlank(userQueryRequest.getUserName()), "userName", userQueryRequest.getUserName());
        queryWrapper.like(StringUtils.isNotBlank(userQueryRequest.getUserProfile()), "userProfile", userQueryRequest.getUserProfile());
        queryWrapper.eq(StringUtils.isNotBlank(userQueryRequest.getUserRole()), "userRole", userQueryRequest.getUserRole());

        // 3. 添加排序条件
        queryWrapper.orderBy(
                SqlUtils.validSortField(userQueryRequest.getSortField()),
                SortConstant.SORT_ORDER_ASC.equals(userQueryRequest.getSortOrder()),
                userQueryRequest.getSortField()
        );

        // 4. 返回构建好的查询条件
        return queryWrapper;
    }
}

package com.zephyr.springboottemplate.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zephyr.springboottemplate.annotation.AuthCheck;
import com.zephyr.springboottemplate.common.BaseResponse;
import com.zephyr.springboottemplate.common.DeleteRequest;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.config.WxOpenConfig;
import com.zephyr.springboottemplate.constant.UserConstant;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.model.dto.user.*;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.vo.LoginUserVO;
import com.zephyr.springboottemplate.model.vo.UserVO;
import com.zephyr.springboottemplate.service.UserService;
import com.zephyr.springboottemplate.utils.ThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.zephyr.springboottemplate.service.impl.UserServiceImpl.SALT;


@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户通过账号、密码和确认密码进行注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return new BaseResponse<>(0, result, "ok");
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过账号和密码登录系统，返回用户信息")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return new BaseResponse<>(0, loginUserVO, "ok");
    }

    @PostMapping("/loginByWxOpen")
    @Operation(summary = "微信登录", description = "通过微信登录，返回用户信息")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            LoginUserVO loginUserVO = userService.userLoginByMpOpen(userInfo, request);
            return new BaseResponse<>(0, loginUserVO, "ok");
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出系统")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return new BaseResponse<>(0, result, "ok");
    }

    @GetMapping("get/loginUser")
    @Operation(summary = "获取登录用户", description = "获取当前登录用户信息")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        return new BaseResponse<>(0, loginUserVO, "ok");
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户", description = "添加用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());

        user.setUserPassword(encryptPassword);
        boolean saveResult = userService.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
        return new BaseResponse<>(0, user.getId(), "ok");
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户", description = "删除用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean removeResult = userService.removeById(deleteRequest.getId());
        return new BaseResponse<>(0, removeResult, "ok");
    }

    @PostMapping("/update")
    @Operation(summary = "更新用户", description = "更新用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(updateRequest, user);
        boolean updateResult = userService.updateById(user);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return new BaseResponse<>(0, true, "ok");
    }

    @GetMapping("/get")
    @Operation(summary = "获取用户", description = "获取用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return new BaseResponse<>(0, user, "ok");
    }

    @GetMapping("get/vo")
    @Operation(summary = "获取用户VO", description = "获取用户VO信息")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> userResponse = getUserById(id);
        User user = userResponse.getData();
        UserVO userVO = userService.getUserVO(user);
        return new BaseResponse<>(0, userVO, "ok");
    }

    @PostMapping("list/page")
    @Operation(summary = "分页获取用户", description = "分页获取用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long currentNum = userQueryRequest.getCurrentNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> page = new Page<>(currentNum, pageSize);
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userQueryRequest);
        Page<User> userPage = userService.page(page, queryWrapper);
        return new BaseResponse<>(0, userPage, "ok");
    }

    @PostMapping("list/page/vo")
    @Operation(summary = "分页获取用户VO", description = "分页获取用户VO信息")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long currentNum = userQueryRequest.getCurrentNum();
        long pageSize = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = new Page<>(currentNum, pageSize);
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userQueryRequest);
        Page<UserVO> userVOPage = new Page<>(currentNum, pageSize, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return new BaseResponse<>(0, userVOPage, "ok");
    }

    @PostMapping("update/my")
    @Operation(summary = "更新当前用户", description = "更新当前用户信息")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean updateResult = userService.updateById(user);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return new BaseResponse<>(0, true, "ok");
    }
}

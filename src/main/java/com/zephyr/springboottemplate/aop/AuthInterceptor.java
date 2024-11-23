package com.zephyr.springboottemplate.aop;

import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.enums.UserRoleEnum;
import com.zephyr.springboottemplate.service.UserService;
import com.zephyr.springboottemplate.annotation.AuthCheck;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 权限校验拦截器
 *
 * 使用AOP拦截带有@AuthCheck注解的方法，校验用户是否具有指定的权限。
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 拦截方法并进行权限校验
     *
     * @param joinPoint 方法的连接点（被拦截的方法）
     * @param authCheck 自定义注解AuthCheck，包含方法所需权限信息
     * @return 方法执行的返回值
     * @throws Throwable 如果用户没有权限或发生其他异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 从注解中获取必须的角色
        String mustRole = authCheck.mustRole();

        // 获取当前请求对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 获取必须具备的角色枚举
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 如果方法不需要特定权限，直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        // 获取当前用户的角色枚举
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        // 如果用户角色无效，抛出权限异常
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 如果用户角色为封禁状态，抛出权限异常
        if (UserRoleEnum.BAN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 如果必须是管理员权限，校验用户是否为管理员
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum)) {
            if (!UserRoleEnum.ADMIN.equals(userRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 通过权限校验，执行被拦截的方法
        return joinPoint.proceed();
    }
}
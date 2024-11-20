package com.zephyr.springboottemplate.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义注解：AuthCheck
 * 用于在方法级别进行权限校验，通过指定必须的角色（mustRole）来限制访问。
 *
 * <p>
 * 使用场景：
 * 1. 标注在方法上，表示该方法需要特定角色才能访问。
 * 2. 配合 AOP（切面编程）或框架功能，动态解析注解实现权限校验。
 */
@Target(ElementType.METHOD) // 目标是方法
@Retention(RetentionPolicy.RUNTIME) // 在运行时保留
public @interface AuthCheck {

    /**
     * 必须具备的角色
     *
     * <p>
     * 用于指定访问该方法所需要的用户角色。
     * 例如：管理员角色为 "admin"，普通用户角色为 "user"。
     * 默认值为空字符串，表示不限定角色。
     * </p>
     *
     * @return 必须具备的角色名称
     */
    String mustRole() default "";
}
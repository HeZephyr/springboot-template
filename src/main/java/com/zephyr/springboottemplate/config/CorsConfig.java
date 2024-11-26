package com.zephyr.springboottemplate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置类
 *
 * <p>
 * 用于配置跨域资源共享（CORS, Cross-Origin Resource Sharing）的全局策略，
 * 解决前后端分离项目中浏览器的跨域请求限制问题。
 * </p>
 * <p>
 * 主要功能：
 * 1. 配置允许的跨域请求路径、来源、方法、请求头、响应头等。
 * 2. 适用于所有 Controller 的跨域请求。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置全局跨域规则
     *
     * @param registry 用于注册跨域请求规则的注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 匹配所有的请求路径，适用于所有接口
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行的域名，允许所有来源的请求
                .allowedOriginPatterns("*")
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 暴露的响应头
                .exposedHeaders("*");
    }
}
package com.zephyr.springboottemplate.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信开放平台配置类
 * 用于配置和初始化微信公众平台服务（WxMpService），通过加载配置文件中的 appId 和 appSecret 生成对应的服务实例。
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "wx.open")
@Data
public class WxOpenConfig {
    /**
     * 微信开放平台的 AppId，需在配置文件中设置。
     */
    private String appId;

    /**
     * 微信开放平台的 AppSecret，需在配置文件中设置。
     */
    private String appSecret;

    /**
     * 微信公众平台服务实例，懒加载实现。
     */
    private WxMpService wxMpService;

    /**
     * 获取微信公众平台服务实例。
     * 使用双重检查锁（Double-Checked Locking）确保线程安全并优化性能。
     *
     * @return 初始化后的 WxMpService 实例
     */
    public WxMpService getWxMpService() {
        // 如果服务实例已经初始化，直接返回
        if (wxMpService != null) {
            return wxMpService;
        }
        // 加锁，确保多线程环境下安全
        synchronized (this) {
            // 再次检查实例是否已初始化（双重检查锁）
            if (wxMpService != null) {
                return wxMpService;
            }
            // 创建微信配置对象并设置 appId 和 appSecret
            WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(appId);
            config.setSecret(appSecret);

            // 创建微信公众平台服务实例并设置配置
            WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(config);

            // 将服务实例赋值给类属性
            wxMpService = service;

            // 返回服务实例
            return wxMpService;
        }
    }
}
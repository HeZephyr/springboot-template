package com.zephyr.springboottemplate.wxmp.router;

import com.zephyr.springboottemplate.wxmp.constant.WxMpConstant;
import com.zephyr.springboottemplate.wxmp.handler.EventHandler;
import com.zephyr.springboottemplate.wxmp.handler.MessageHandler;
import com.zephyr.springboottemplate.wxmp.handler.SubscribeHandler;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号消息路由配置类
 *
 * <p>
 * 使用 `@Configuration` 定义为配置类，用于初始化消息路由器 `WxMpMessageRouter`。
 * 消息路由器根据消息类型和事件类型将消息路由到对应的处理器。
 * </p>
 */
@Configuration
public class WxMpMsgRouter {

    @Resource
    private WxMpService wxMpService; // 微信公众号服务接口，用于处理微信公众号的核心逻辑

    @Resource
    private EventHandler eventHandler; // 自定义事件处理器，用于处理点击菜单等事件

    @Resource
    private MessageHandler messageHandler; // 自定义消息处理器，用于处理普通文本消息

    @Resource
    private SubscribeHandler subscribeHandler; // 自定义订阅处理器，用于处理用户关注事件

    /**
     * 配置并初始化微信消息路由器
     *
     * <p>
     * - 消息路由器基于规则匹配，将不同类型的消息转发到对应的处理器。
     * - 支持文本消息、事件消息（如用户订阅、点击菜单等）。
     * </p>
     *
     * @return 已配置的 `WxMpMessageRouter` 对象
     */
    @Bean
    public WxMpMessageRouter getWxMsgRouter() {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService); // 创建消息路由器实例，依赖于 WxMpService

        // 配置文本消息的处理规则
        router.rule()
                .async(false) // 是否异步处理消息
                .msgType(XmlMsgType.TEXT) // 仅处理文本消息
                .handler(messageHandler) // 指定处理器为 MessageHandler
                .end(); // 结束当前规则配置

        // 配置关注事件的处理规则
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT) // 仅处理事件类型的消息
                .event(EventType.SUBSCRIBE) // 事件类型为用户订阅
                .handler(subscribeHandler) // 指定处理器为 SubscribeHandler
                .end();

        // 配置点击菜单事件的处理规则
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT) // 事件类型消息
                .event(EventType.CLICK) // 事件类型为点击菜单
                .eventKey(WxMpConstant.CLICK_MENU_KEY) // 指定事件 Key
                .handler(eventHandler) // 指定处理器为 EventHandler
                .end();

        return router; // 返回配置好的路由器
    }
}
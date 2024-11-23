package com.zephyr.springboottemplate.wxmp.handler;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信公众号事件处理器
 *
 * <p>
 * 此类实现了 {@link WxMpMessageHandler} 接口，用于处理微信事件消息。当前示例响应用户点击菜单事件，
 * 并返回一条文本消息提示用户操作。
 * </p>
 *
 * <p>
 * 主要作用：
 * 1. 解析接收到的微信事件消息（如菜单点击、关注等）。
 * 2. 根据事件类型执行具体逻辑，例如返回消息、调用其他服务等。
 * 3. 构建并返回一个响应的微信消息。
 * </p>
 *
 * <p>
 * 核心逻辑：
 * - 从消息中获取发送方和接收方的用户信息。
 * - 根据事件类型构建文本响应消息。
 * - 支持扩展以处理不同类型的微信事件。
 * </p>
 *
 * <p>
 * 示例：当用户点击菜单时，返回“您点击了菜单”提示信息。
 * </p>
 */
@Component
public class EventHandler implements WxMpMessageHandler {

    /**
     * 处理微信事件消息
     *
     * @param wxMpXmlMessage  微信平台传递的消息对象，包含事件类型、用户信息等
     * @param map             附加参数，可以用于传递自定义信息
     * @param wxMpService     微信服务接口，用于调用微信API
     * @param wxSessionManager 微信会话管理器，可用于管理用户会话
     * @return 构建的响应消息对象 {@link WxMpXmlOutMessage}
     * @throws WxErrorException 微信API异常
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService,
                                    WxSessionManager wxSessionManager) throws WxErrorException {
        // 定义返回的文本内容
        final String content = "您点击了菜单";

        // 构建并返回一个文本消息对象，包含接收方和发送方的信息
        return WxMpXmlOutMessage.TEXT().content(content) // 设置文本内容
                .fromUser(wxMpXmlMessage.getToUser()) // 设置消息发送者（公众号）
                .toUser(wxMpXmlMessage.getFromUser()) // 设置消息接收者（用户）
                .build(); // 构建最终的消息对象
    }
}
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
 * 微信公众号订阅事件处理器
 *
 * <p>
 * 该处理器用于处理用户订阅公众号的事件。
 * 当用户关注公众号时，系统会返回一条欢迎消息。
 * </p>
 */
@Component
public class SubscribeHandler implements WxMpMessageHandler {

    /**
     * 处理用户订阅事件
     *
     * @param wxMpXmlMessage  收到的微信消息对象，包含用户信息、事件类型等
     * @param map             消息上下文，可用于传递自定义参数
     * @param wxMpService     微信公众号相关的服务接口
     * @param wxSessionManager 微信会话管理器
     * @return WxMpXmlOutMessage 构建的回复消息
     * @throws WxErrorException 如果处理过程中出现微信接口错误
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService,
                                    WxSessionManager wxSessionManager) throws WxErrorException {
        // 定义回复的欢迎内容
        final String content = "感谢关注";

        // 构建并返回文本类型的回复消息
        return WxMpXmlOutMessage.TEXT()
                .content(content) // 设置欢迎内容
                .fromUser(wxMpXmlMessage.getToUser()) // 指定发送方（公众号）
                .toUser(wxMpXmlMessage.getFromUser()) // 指定接收方（用户）
                .build(); // 构建消息
    }
}
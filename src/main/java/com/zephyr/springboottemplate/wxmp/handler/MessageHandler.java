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
 * 微信公众号消息处理器 - 用于处理用户发送的文本消息
 *
 * <p>
 * 本处理器实现了“复读机”功能，将用户发送的文本消息内容进行简单加工后返回。
 * </p>
 */
@Component
public class MessageHandler implements WxMpMessageHandler {

    /**
     * 处理微信文本消息
     *
     * @param wxMpXmlMessage  收到的微信消息对象，包含消息内容、发送方信息等
     * @param map             消息上下文，可以传递自定义参数
     * @param wxMpService     微信公众号相关的服务接口
     * @param wxSessionManager 微信会话管理器
     * @return WxMpXmlOutMessage 构建的回复消息
     * @throws WxErrorException 如果处理过程中出现微信接口错误
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService,
                                    WxSessionManager wxSessionManager) throws WxErrorException {
        // 获取用户发送的文本内容
        String content = "我是复读机：" + wxMpXmlMessage.getContent();

        // 构建返回消息，类型为文本
        return WxMpXmlOutMessage.TEXT().content(content) // 设置回复的文本内容
                .fromUser(wxMpXmlMessage.getToUser()) // 指定发送方（公众号）
                .toUser(wxMpXmlMessage.getFromUser()) // 指定接收方（用户）
                .build(); // 构建消息
    }
}
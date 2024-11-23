package com.zephyr.springboottemplate.controller;

import com.zephyr.springboottemplate.wxmp.constant.WxMpConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * 微信公众号控制器
 *
 * <p>
 * 提供与微信公众号交互的接口，包括消息接收与回复、菜单管理等功能。
 * </p>
 */
@RestController
@RequestMapping("/wx/mp")
@Slf4j
@Tag(name = "微信公众平台接口", description = "提供与微信公众号交互的 API，例如接收消息、校验请求、设置菜单等功能。")
public class WxMpController {

    @Resource
    private WxMpService wxMpService; // 微信公众号服务，用于处理微信接口调用

    @Resource
    private WxMpMessageRouter router; // 消息路由器，用于将不同的消息类型分发到对应的处理器

    /**
     * 接收并处理微信公众号的消息
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws IOException 可能的 I/O 异常
     */
    @PostMapping("/")
    @Operation(summary = "接收微信消息", description = "接收并处理来自微信服务器的消息，支持明文和加密消息的解析与回复。")
    public void receiveMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // 校验消息签名，确保消息来自微信服务器
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            response.getWriter().println("非法请求");
            return;
        }

        // 获取消息加密类型（raw 或 aes）
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ? "raw"
                : request.getParameter("encrypt_type");

        // 明文消息（不加密）直接返回
        if ("raw".equals(encryptType)) {
            return;
        }

        // AES 加密消息
        if ("aes".equals(encryptType)) {
            // 解密消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage
                    .fromEncryptedXml(request.getInputStream(), wxMpService.getWxMpConfigStorage(), timestamp,
                            nonce, msgSignature);
            log.info("message content = {}", inMessage.getContent());

            // 使用路由器处理消息
            WxMpXmlOutMessage outMessage = router.route(inMessage);
            if (outMessage == null) {
                response.getWriter().write("");
            } else {
                // 返回加密后的响应消息
                response.getWriter().write(outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage()));
            }
            return;
        }

        // 如果加密类型不可识别，返回错误提示
        response.getWriter().println("不可识别的加密类型");
    }

    /**
     * 校验微信服务器的连接请求
     *
     * <p>
     * 微信服务器会通过 GET 请求调用此接口，用于验证开发者服务器的有效性。
     * </p>
     *
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param signature 签名
     * @param echostr   随机字符串回显
     * @return 如果校验成功，返回 echostr；否则返回空字符串
     */
    @GetMapping("/")
    @Operation(summary = "验证微信请求", description = "接收微信服务器的验证请求，通过验证签名返回 `echostr` 以确保请求合法性。")
    public String check(String timestamp, String nonce, String signature, String echostr) {
        log.info("check");
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        } else {
            return "";
        }
    }

    /**
     * 设置微信公众号菜单
     *
     * <p>
     * 定义微信公众号的自定义菜单，包括菜单项和子菜单项。
     * </p>
     *
     * @return 操作成功的消息
     * @throws WxErrorException 如果微信接口调用失败
     */
    @GetMapping("/setMenu")
    @Operation(summary = "设置微信公众号菜单", description = "通过微信 API 定义自定义菜单，并设置到微信公众号中。")
    public String setMenu() throws WxErrorException {
        log.info("setMenu");

        // 创建菜单对象
        WxMenu wxMenu = new WxMenu();

        // 菜单一：主菜单 + 子菜单
        WxMenuButton wxMenuButton1 = new WxMenuButton();
        wxMenuButton1.setType(WxConsts.MenuButtonType.VIEW);
        wxMenuButton1.setName("主菜单一");

        WxMenuButton wxMenuButton1SubButton1 = new WxMenuButton();
        wxMenuButton1SubButton1.setType(WxConsts.MenuButtonType.VIEW);
        wxMenuButton1SubButton1.setName("跳转页面");
        wxMenuButton1SubButton1.setUrl("https://hezephyr.github.io");
        wxMenuButton1.setSubButtons(Collections.singletonList(wxMenuButton1SubButton1));

        // 菜单二：点击事件
        WxMenuButton wxMenuButton2 = new WxMenuButton();
        wxMenuButton2.setType(WxConsts.MenuButtonType.CLICK);
        wxMenuButton2.setName("点击事件");
        wxMenuButton2.setKey(WxMpConstant.CLICK_MENU_KEY);

        // 菜单三：主菜单 + 子菜单
        WxMenuButton wxMenuButton3 = new WxMenuButton();
        wxMenuButton3.setType(WxConsts.MenuButtonType.VIEW);
        wxMenuButton3.setName("主菜单三");

        WxMenuButton wxMenuButton3SubButton1 = new WxMenuButton();
        wxMenuButton3SubButton1.setType(WxConsts.MenuButtonType.VIEW);
        wxMenuButton3SubButton1.setName("编程学习");
        wxMenuButton3SubButton1.setUrl("https://hezephyr.github.io");
        wxMenuButton3.setSubButtons(Collections.singletonList(wxMenuButton3SubButton1));

        // 设置菜单的按钮
        wxMenu.setButtons(Arrays.asList(wxMenuButton1, wxMenuButton2, wxMenuButton3));

        // 调用微信接口创建菜单
        wxMpService.getMenuService().menuCreate(wxMenu);

        return "ok";
    }
}
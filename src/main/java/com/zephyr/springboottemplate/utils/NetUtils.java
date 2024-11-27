package com.zephyr.springboottemplate.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * 网络工具类
 * <p>
 * 功能描述：
 * 1. 提供与网络相关的工具方法。
 * 2. 当前实现了获取客户端 IP 地址的方法。
 * </p>
 * 使用场景：
 * 1. 获取客户端的真实 IP 地址，用于日志记录、安全审计等场景。
 * 2. 适配多级代理情况下的真实 IP 提取。
 * <p>
 * 注意事项：
 * 1. 如果通过代理服务器访问，需确保代理服务器正确设置了请求头信息。
 * 2. 在某些环境中，可能会有特定的代理配置，需适配额外的请求头字段。
 * </p>
 */
@Slf4j
public class NetUtils {

    /**
     * 获取客户端 IP 地址
     * <p>
     * 功能：
     * 1. 从 Http 请求中提取客户端的 IP 地址。
     * 2. 适配多种代理场景，确保获取真实的客户端 IP。
     * </p>
     *
     * <p>
     * 获取顺序：
     * 1. 检查 "x-forwarded-for" 请求头（常用于负载均衡和代理）。
     * 2. 检查 "Proxy-Client-IP" 和 "WL-Proxy-Client-IP" 请求头。
     * 3. 如果没有代理，直接获取 `request.getRemoteAddr()`。
     * 4. 如果是本地访问（127.0.0.1），获取本机实际 IP 地址。
     * 5. 处理多级代理的情况，仅保留第一个 IP。
     * </p>
     * 特殊情况处理：
     * 1. 如果无法获取 IP 地址，返回默认值 "127.0.0.1"。
     *
     * @param request HTTP 请求对象
     * @return 客户端的 IP 地址，默认为 "127.0.0.1"（本地地址）
     */
    public static String getIpAddress(HttpServletRequest request) {
        // 获取 "x-forwarded-for" 请求头的值（常用于负载均衡和代理服务器）
        String ip = request.getHeader("x-forwarded-for");

        // 如果 "x-forwarded-for" 为空，尝试获取其他代理相关的请求头
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        // 如果以上请求头均为空，直接获取远程地址
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();

            // 如果远程地址为本地地址 (127.0.0.1)，获取本机实际 IP 地址
            if ("127.0.0.1".equals(ip)) {
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    if (inet != null) {
                        ip = inet.getHostAddress(); // 获取本机 IP
                    }
                } catch (Exception e) {
                    log.error("Get local IP address failed: {}", e.getMessage());
                }
            }
        }

        // 如果 IP 地址有多个代理，保留第一个（第一个通常为真实客户端 IP）
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }

        // 如果 IP 仍然为空，返回默认值 "127.0.0.1"
        if (ip == null) {
            return "127.0.0.1";
        }

        // 返回最终获取的 IP 地址
        return ip;
    }
}
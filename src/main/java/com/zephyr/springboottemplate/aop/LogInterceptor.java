package com.zephyr.springboottemplate.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * 日志拦截器
 * <p>
 * 使用 AOP 实现对所有控制器方法的日志记录，包括请求路径、参数、执行时间等。
 */
@Aspect
@Slf4j
@Component
public class LogInterceptor {

    /**
     * 拦截所有控制器方法，并记录日志
     *
     * @param joinPoint AOP 连接点，代表被拦截的方法
     * @return 方法的返回值
     * @throws Throwable 如果被拦截的方法抛出异常
     */
    @Around("execution(* com.zephyr.springboottemplate.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 计时器，用于计算方法执行时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取当前请求的相关信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 生成请求唯一 ID，用于标识每个请求
        String requestId = UUID.randomUUID().toString();
        // 获取请求的 URL
        String url = httpServletRequest.getRequestURI();

        // 获取请求参数
        Object[] args = joinPoint.getArgs(); // 方法的参数
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";

        // 记录请求日志
        log.info("request start, id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);

        // 执行被拦截的方法
        Object result = joinPoint.proceed();

        // 记录响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);

        // 返回方法的执行结果
        return result;
    }
}
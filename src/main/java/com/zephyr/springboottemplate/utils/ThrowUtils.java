package com.zephyr.springboottemplate.utils;

import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.exception.BusinessException;

/**
 * 抛异常工具类
 * <p>
 * 用于在代码中简化条件判断下的异常抛出逻辑，避免重复编写类似代码，提高可读性。
 * 提供多种重载方法支持不同类型的异常抛出。
 */
public class ThrowUtils {

    /**
     * 条件满足时抛出指定的运行时异常。
     *
     * @param condition       条件判断结果，如果为 true 则抛出异常
     * @param runtimeException 要抛出的运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException; // 抛出指定的异常
        }
    }

    /**
     * 条件满足时抛出指定的业务异常。
     * 使用统一的错误码定义，便于异常信息的标准化管理。
     *
     * @param condition 条件判断结果，如果为 true 则抛出异常
     * @param errorCode 错误码，封装了异常的具体信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode)); // 构造并抛出业务异常
    }

    /**
     * 条件满足时抛出指定的业务异常，并包含额外的消息信息。
     * 支持自定义错误消息，便于描述具体异常的上下文信息。
     *
     * @param condition 条件判断结果，如果为 true 则抛出异常
     * @param errorCode 错误码，封装了异常的具体信息
     * @param message   自定义错误消息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message)); // 构造并抛出业务异常
    }
}
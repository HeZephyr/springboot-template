package com.zephyr.springboottemplate.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * <p>
 * 用于定义和管理文件上传的业务类型，例如用户头像和文档类型。
 * 枚举包含业务名称（text）和对应的值（value），并提供相关的辅助方法。
 * </p>
 */
@AllArgsConstructor
@Getter
public enum FileUploadBizEnum {

    /**
     * 用户头像上传业务类型
     */
    USER_AVATAR("用户头像", "user_avatar"),

    /**
     * 文档上传业务类型
     */
    DOCUMENT("文档", "document");

    /**
     * 业务类型描述文本
     */
    private final String text;

    /**
     * 业务类型对应的值
     */
    private final String value;

    /**
     * 获取所有业务类型的值列表
     *
     * <p>
     * 例如：返回 ["user_avatar", "document"]
     * </p>
     *
     * @return 所有业务类型的值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()) // 获取枚举值列表
                .map(item -> item.value) // 提取每个枚举的 value
                .collect(Collectors.toList()); // 转换为列表
    }

    /**
     * 根据 value 获取对应的枚举实例
     *
     * <p>
     * 例如：传入 "user_avatar"，返回 USER_AVATAR 枚举实例。
     * 如果传入的值为空或不存在对应的枚举，则返回 null。
     * </p>
     *
     * @param value 枚举值
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static FileUploadBizEnum getEnumByValue(String value) {
        // 判断 value 是否为空
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        // 遍历所有枚举值并查找匹配的 value
        for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null; // 未找到匹配的枚举实例，返回 null
    }

    /**
     * 格式化枚举的字符串表示
     *
     * <p>
     * 返回枚举的文本和值的组合，例如："用户头像 (user_avatar)"
     * </p>
     *
     * @return 枚举的字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", text, value); // 格式化为 "文本 (值)"
    }
}
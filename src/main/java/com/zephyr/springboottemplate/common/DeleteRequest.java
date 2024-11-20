package com.zephyr.springboottemplate.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用删除请求类
 * 用于封装删除操作的请求数据，通常在删除接口中作为请求参数使用。
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * 要删除的实体的唯一标识（主键 ID）。
     */
    private Long id;

    /**
     * 序列化版本号
     * 用于在对象序列化和反序列化过程中验证一致性。
     */
    @Serial
    private static final long serialVersionUID = 1L;
}
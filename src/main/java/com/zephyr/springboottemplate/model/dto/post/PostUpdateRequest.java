package com.zephyr.springboottemplate.model.dto.post;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子更新请求
 */
@Data
public class PostUpdateRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    @Serial
    private static final long serialVersionUID = 1L;
}

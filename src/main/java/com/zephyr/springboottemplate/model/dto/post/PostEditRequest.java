package com.zephyr.springboottemplate.model.dto.post;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子编辑请求
 */
@Data
public class PostEditRequest implements Serializable {

    /**
     * 帖子ID
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

package com.zephyr.springboottemplate.model.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 帖子收藏
 */
@Data
public class PostCollection implements Serializable {
    /**
     * 收藏id
     */
    private Long id;
    /**
     * 帖子id
     */
    private Long postId;
    /**
     * 创建用户id
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    @Serial
    private static final long serialVersionUID = 1L;
}

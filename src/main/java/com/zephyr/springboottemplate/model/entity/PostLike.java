package com.zephyr.springboottemplate.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class PostLike implements Serializable {
    /**
     * 点赞id
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

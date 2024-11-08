package com.zephyr.springboottemplate.model.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 帖子
 */
@Data
public class Post implements Serializable {
    /**
     * 帖子id
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
     * 标签列表 json
     */
    private String tags;
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
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 收藏数
     */
    private Integer collectCount;
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    @Serial
    private static final long serialVersionUID = 1L;
}

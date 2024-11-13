package com.zephyr.springboottemplate.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 帖子收藏
 */
@Data
@TableName(value = "post_collection")
public class PostCollection implements Serializable {
    /**
     * 收藏id
     */
    @TableId(type = IdType.AUTO) // 标识该字段为表的主键，type = IdType.AUTO表示主键自增
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
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

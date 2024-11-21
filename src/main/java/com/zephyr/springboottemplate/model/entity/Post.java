package com.zephyr.springboottemplate.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 帖子
 */
@Data
@TableName(value = "post") // 标识该类对应数据库中的表名
public class Post implements Serializable {
    /**
     * 帖子id
     */
    @TableId(type = IdType.ASSIGN_ID) // 标识该字段为表的主键，type = IdType.ASSIGN_ID 表示 MyBatis-Plus 在应用层使用生成器自动生成全局唯一 ID
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
    @TableLogic // 标识该字段为逻辑删除字段，即不会真正删除数据，而是通过标记删除状态来实现删除
    private Boolean isDelete;
    @Serial
    @TableField(exist = false) // 标识该字段不对应数据库中的列
    private static final long serialVersionUID = 1L;
}

package com.zephyr.springboottemplate.model.dto.post;

import com.zephyr.springboottemplate.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子查询请求
 *
 * 封装了查询帖子时可能使用的各种查询条件，支持多条件、分页等灵活查询。
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PostQueryRequest extends PageRequest implements Serializable {

    /**
     * 帖子ID，用于精确查询某一特定帖子
     */
    private Long id;

    /**
     * 排除的帖子ID，在查询结果中排除指定的帖子
     */
    private Long notId;

    /**
     * 搜索词，支持根据标题和内容的关键字进行全文搜索
     */
    private String searchText;

    /**
     * 帖子标题，用于按标题进行精确或模糊查询
     */
    private String title;

    /**
     * 帖子内容，用于按内容进行精确或模糊查询
     */
    private String content;

    /**
     * 标签列表，要求查询结果包含所有指定标签
     */
    private List<String> tags;

    /**
     * 至少包含一个标签的标签列表，满足查询结果至少包含其中一个指定标签
     */
    private List<String> orTags;

    /**
     * 创建该帖子的用户ID，用于按用户查询其发布的帖子
     */
    private Long userId;

    /**
     * 收藏该帖子的用户ID，用于查询某个用户收藏的帖子
     */
    private Long collectUserId;

    /**
     * 序列化ID，确保对象在序列化和反序列化时的版本一致性
     */
    @Serial
    private static final long serialVersionUID = 1L;
}
package com.zephyr.springboottemplate.model.dto.postcollection;

import com.zephyr.springboottemplate.common.PageRequest;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子收藏查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCollectionQueryRequest extends PageRequest implements Serializable {

    /**
     * 帖子查询请求
     */
    private PostQueryRequest postQueryRequest;

    /**
     * 用户 ID
     */
    private Long userId;

    @Serial
    private static final long serialVersionUID = 1L;
}

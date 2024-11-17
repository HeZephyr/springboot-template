package com.zephyr.springboottemplate.model.dto.postcollection;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子收藏 / 取消收藏
 */
public class PostCollectionAddRequest implements Serializable {
    /**
     * 帖子 id
     */
    private Long postId;
    @Serial
    private static final long serialVersionUID = 1L;
}

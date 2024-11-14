package com.zephyr.springboottemplate.model.dto.postlike;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子点赞请求
 */
@Data
public class PostLikeAddRequest implements Serializable {
    /**
     * 帖子ID
     */
    private Long postId;

    @Serial
    private static final long serialVersionUID = 1L;
}

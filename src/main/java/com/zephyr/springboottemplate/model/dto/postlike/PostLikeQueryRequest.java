package com.zephyr.springboottemplate.model.dto.postlike;

import com.zephyr.springboottemplate.common.PageRequest;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostLikeQueryRequest extends PageRequest implements Serializable {
    private PostQueryRequest postQueryRequest;
    private Long userId;
    @Serial
    private static final long serialVersionUID = 1L;
}

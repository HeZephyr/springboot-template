package com.zephyr.springboottemplate.model.dto.post;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.zephyr.springboottemplate.model.entity.Post;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 数据传输对象（DTO） - Elasticsearch 索引对应的 Post 数据
 * 用于与 Elasticsearch 进行交互
 */
// todo 取消注释开启 ES（须先配置 ES）
// @Document(indexName = "post")
@Data
public class PostEsDTO implements Serializable {

    /**
     * 日期时间格式，遵循 ISO 8601 标准
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 主键 ID
     */
    @Id
    private Long id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 帖子标签列表
     */
    private List<String> tags;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 发布用户 ID
     */
    private Long userId;

    /**
     * 创建时间
     * 不建立索引，仅存储，用于排序或展示
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     * 不建立索引，仅存储，用于排序或展示
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否被删除标志
     */
    private Integer isDelete;

    /**
     * 序列化 ID，用于兼容不同版本间的序列化反序列化
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 将实体对象 Post 转换为 Elasticsearch DTO
     *
     * @param post 实体对象
     * @return PostEsDTO DTO 对象
     */
    public static PostEsDTO objToDto(Post post) {
        if (post == null) {
            return null;
        }
        PostEsDTO postEsDTO = new PostEsDTO();
        BeanUtils.copyProperties(post, postEsDTO);
        String tagsStr = post.getTags();
        // 将 JSON 格式的字符串转换为 List<String>
        if (StringUtils.isNotBlank(tagsStr)) {
            postEsDTO.setTags(JSONUtil.toList(tagsStr, String.class));
        }
        return postEsDTO;
    }

    /**
     * 将 Elasticsearch DTO 转换为实体对象 Post
     *
     * @param postEsDTO DTO 对象
     * @return Post 实体对象
     */
    public static Post dtoToObj(PostEsDTO postEsDTO) {
        if (postEsDTO == null) {
            return null;
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEsDTO, post);
        List<String> tags = postEsDTO.getTags();
        // 将 List<String> 转换为 JSON 格式的字符串
        if (CollUtil.isNotEmpty(tags)) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        return post;
    }
}
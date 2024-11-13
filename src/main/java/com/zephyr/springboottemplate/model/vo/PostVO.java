package com.zephyr.springboottemplate.model.vo;

import cn.hutool.json.JSONUtil;
import com.zephyr.springboottemplate.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 */
@Data
public class PostVO implements Serializable {
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
     * 标签列表
     */
    private List<String> tagList;

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
     * 创建人信息
     */
    private UserVO user;

    /**
     * 是否已点赞
     */
    private Boolean hasLike;

    /**
     * 是否已收藏
     */
    private Boolean hasCollect;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 将包装类（PostVO）转换为实体类（Post）
     *
     * @param postVO 包装类对象，用于封装前端传递的数据
     * @return 转换后的实体类对象，如果传入的 postVO 为 null，则返回 null
     */
    public static Post voToObj(PostVO postVO) {
        // 如果 postVO 为空，则直接返回 null
        if (postVO == null) {
            return null;
        }
        // 创建新的 Post 实体对象
        Post post = new Post();
        // 复制 postVO 中的属性到 post 中
        BeanUtils.copyProperties(postVO, post);

        // 获取 postVO 的标签列表，将其转换为 JSON 字符串后赋值给 post 的 tags 字段
        List<String> tagList = postVO.getTagList();
        post.setTags(JSONUtil.toJsonStr(tagList));

        // 返回转换后的实体类对象
        return post;
    }

    /**
     * 将实体类（Post）转换为包装类（PostVO）
     *
     * @param post 实体类对象，用于数据库中的数据表示
     * @return 转换后的包装类对象，如果传入的 post 为 null，则返回 null
     */
    public static PostVO objToVo(Post post) {
        // 如果 post 为空，则直接返回 null
        if (post == null) {
            return null;
        }
        // 创建新的 PostVO 包装对象
        PostVO postVO = new PostVO();
        // 复制 post 中的属性到 postVO 中
        BeanUtils.copyProperties(post, postVO);

        // 获取 post 的 tags 字段，将 JSON 字符串转换为 List<String> 并赋值给 postVO 的 tagList
        postVO.setTagList(JSONUtil.toList(post.getTags(), String.class));

        // 返回转换后的包装类对象
        return postVO;
    }
}

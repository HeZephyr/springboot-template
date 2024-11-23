package com.zephyr.springboottemplate.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostLike;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子点赞数据库操作
 */
public interface PostLikeMapper extends BaseMapper<PostLike> {
    Page<Post> listLikedPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper, long likedUserId);
}

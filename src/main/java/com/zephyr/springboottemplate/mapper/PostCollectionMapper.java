package com.zephyr.springboottemplate.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostCollection;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子收藏数据库操作
 */
public interface PostCollectionMapper extends BaseMapper<PostCollection> {
    Page<Post> listCollectedPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper, long collectedUserId);
}

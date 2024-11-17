package com.zephyr.springboottemplate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zephyr.springboottemplate.model.entity.Post;

import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作
 */
public interface PostMapper extends BaseMapper<Post> {
    /**
     * 查询帖子列表（包括已被删除的数据）
     *
     * @param minUpdateTime 最小更新时间，用于筛选更新日期在此时间之后的帖子
     * @return 符合条件的帖子列表，包括已被删除的数据
     */
    List<Post> listPostWithDelete(Date minUpdateTime);
}

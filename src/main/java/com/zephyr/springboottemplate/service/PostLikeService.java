package com.zephyr.springboottemplate.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostLike;
import com.zephyr.springboottemplate.model.entity.User;

/**
 * 帖子点赞服务接口
 * 提供与帖子点赞相关的业务逻辑操作
 */
public interface PostLikeService extends IService<PostLike> {

    /**
     * 执行帖子点赞操作
     * 如果用户已经点赞，则取消点赞；如果未点赞，则添加点赞记录
     *
     * @param postId   帖子 ID
     * @param loginUser 当前登录用户
     * @return 当前帖子点赞数量
     */
    int doPostLike(long postId, User loginUser);

    /**
     * 内部处理帖子点赞逻辑
     * 提供直接通过用户 ID 和帖子 ID 操作点赞记录的能力
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 当前帖子点赞数量
     */
    int doPostLikeInner(long userId, long postId);

    Page<Post> listLikedPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
                                  long likeUserId);
}
package com.zephyr.springboottemplate.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostCollection;
import com.zephyr.springboottemplate.model.entity.User;

/**
 * 帖子收藏服务接口
 * 提供帖子收藏相关的业务逻辑操作
 */
public interface PostCollectionService extends IService<PostCollection> {

    /**
     * 执行帖子收藏操作
     * 如果用户已经收藏，则取消收藏；如果未收藏，则添加收藏记录
     *
     * @param postId   帖子 ID
     * @param loginUser 当前登录用户
     * @return 收藏状态变化值（1 表示收藏成功，-1 表示取消收藏成功，0 表示失败）
     */
    int doPostCollection(long postId, User loginUser);

    /**
     * 内部处理帖子收藏逻辑
     * 提供直接通过用户 ID 和帖子 ID 操作收藏记录的能力
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 收藏状态变化值（1 表示收藏成功，-1 表示取消收藏成功，0 表示失败）
     */
    int doPostCollectionInner(long userId, long postId);

    /**
     * 分页获取用户的收藏帖子列表
     * 根据指定的分页信息和查询条件，返回用户收藏的帖子列表
     *
     * @param page             分页信息
     * @param queryWrapper     查询条件
     * @param collectionUserId 收藏用户的 ID
     * @return 用户收藏的帖子分页列表
     */
    Page<Post> listCollectedPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long collectionUserId);
}
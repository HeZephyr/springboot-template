package com.zephyr.springboottemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.mapper.PostCollectionMapper;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostCollection;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.service.PostCollectionService;
import com.zephyr.springboottemplate.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * 帖子收藏服务实现类
 */
@Service
public class PostCollectionServiceImpl extends ServiceImpl<PostCollectionMapper, PostCollection> implements PostCollectionService {

    @Resource
    private PostService postService;

    @Override
    public int doPostCollection(long postId, User loginUser) {
        // 1. 检查帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 2. 获取当前用户 ID
        long userId = loginUser.getId();

        // 3. 使用 synchronized 锁定当前用户操作，避免同一用户同时收藏/取消收藏同一帖子
        PostCollectionService postCollectionService = (PostCollectionService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            // 调用内部方法执行收藏操作
            return postCollectionService.doPostCollectionInner(userId, postId);
        }
    }
    @Override
    public int doPostCollectionInner(long userId, long postId) {
        // 1. 构造帖子收藏实体
        PostCollection postCollection = new PostCollection();
        postCollection.setUserId(userId);
        postCollection.setPostId(postId);

        // 2. 查询是否已收藏
        QueryWrapper<PostCollection> postCollectionQueryWrapper = new QueryWrapper<>(postCollection);
        PostCollection oldPostCollection = this.getOne(postCollectionQueryWrapper);
        boolean result;

        // 3. 如果已收藏，则执行取消收藏操作
        if (oldPostCollection != null) {
            result = this.remove(postCollectionQueryWrapper);
            if (result) {
                // 更新收藏数（-1）
                result = postService.update()
                        .eq("id", postId)
                        .gt("collectCount", 0)
                        .setSql("collectCount = collectCount - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 4. 如果未收藏，则执行收藏操作
            result = this.save(postCollection);
            if (result) {
                // 更新收藏数（+1）
                result = postService.update()
                        .eq("id", postId)
                        .setSql("collectCount = collectCount + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    @Override
    public Page<Post> postCollectionListByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long collectedUserId) {
        // 1. 检查用户 ID 合法性
        if (collectedUserId <= 0) {
            return new Page<>();
        }

        // 2. 调用 Mapper 查询收藏帖子
        return baseMapper.listCollectedPostByPage(page, queryWrapper, collectedUserId);
    }
}

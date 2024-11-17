package com.zephyr.springboottemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.mapper.PostLikeMapper;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostLike;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.service.PostLikeService;
import com.zephyr.springboottemplate.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * 帖子点赞服务实现类
 * 提供对帖子点赞和取消点赞的业务逻辑处理
 */
@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {
    @Resource
    private PostService postService; // 注入帖子服务，用于处理帖子相关的逻辑

    @Override
    public int doPostLike(long postId, User loginUser) {
        // 1. 检查帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        long userId = loginUser.getId(); // 获取当前用户的 ID
        // 获取代理对象，用于调用事务方法
        PostLikeService postLikeService = (PostLikeService) AopContext.currentProxy();

        // 2. 同步锁，防止同一用户同时对同一帖子进行并发点赞或取消点赞操作
        synchronized (String.valueOf(userId).intern()) {
            return postLikeService.doPostLikeInner(userId, postId);
        }
    }

    @Override
    public int doPostLikeInner(long userId, long postId) {
        // 1. 构造点赞记录
        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);

        // 2. 查询是否已存在点赞记录
        QueryWrapper<PostLike> postLikeQueryWrapper = new QueryWrapper<>(postLike);
        PostLike oldPostLike = this.getOne(postLikeQueryWrapper);
        boolean result;

        // 3. 如果已点赞，则取消点赞
        if (oldPostLike != null) {
            result = this.remove(postLikeQueryWrapper); // 删除点赞记录
            if (result) {
                // 点赞数减 1
                result = postService.update()
                        .eq("id", postId) // 条件：帖子 ID
                        .gt("likeCount", 0) // 条件：点赞数大于 0
                        .setSql("likeCount = likeCount - 1") // SQL 操作：点赞数减 1
                        .update();
                return result ? -1 : 0; // 返回 -1 表示取消点赞成功，0 表示失败
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "取消点赞失败");
            }
        } else {
            // 4. 如果未点赞，则执行点赞操作
            result = this.save(postLike); // 保存点赞记录
            if (result) {
                // 点赞数加 1
                result = postService.update()
                        .eq("id", postId) // 条件：帖子 ID
                        .setSql("likeCount = likeCount + 1") // SQL 操作：点赞数加 1
                        .update();
                return result ? 1 : 0; // 返回 1 表示点赞成功，0 表示失败
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "点赞失败");
            }
        }
    }
}

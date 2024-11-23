package com.zephyr.springboottemplate.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zephyr.springboottemplate.common.BaseResponse;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import com.zephyr.springboottemplate.model.dto.postlike.PostLikeAddRequest;
import com.zephyr.springboottemplate.model.dto.postlike.PostLikeQueryRequest;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.vo.PostVO;
import com.zephyr.springboottemplate.service.PostLikeService;
import com.zephyr.springboottemplate.service.PostService;
import com.zephyr.springboottemplate.service.UserService;
import com.zephyr.springboottemplate.utils.ThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post_like")
@Slf4j
@Tag(name = "帖子点赞管理", description = "帖子点赞相关操作接口")
public class PostLikeController {

    @Resource
    private PostLikeService postLikeService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @PostMapping("/")
    @Operation(summary = "帖子点赞操作", description = "用户可以点赞指定的帖子，需要登录后操作")
    public BaseResponse<Integer> doPostLike(@RequestBody PostLikeAddRequest postLikeAddRequest,
                                            HttpServletRequest request) {
        if (postLikeAddRequest == null || postLikeAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long postId = postLikeAddRequest.getPostId();
        int result = postLikeService.doPostLike(postId, loginUser);
        return new BaseResponse<>(0, result, "ok");
    }

    @PostMapping("/my/list/page")
    @Operation(summary = "分页获取用户点赞的帖子列表", description = "用户可以分页获取自己点赞过的帖子列表，需登录后操作")
    public BaseResponse<Page<PostVO>> listMyLikedPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                           HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long currentNum = postQueryRequest.getCurrentNum();
        long pageSize = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postLikeService.listLikedPostByPage(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
        return new BaseResponse<>(0, postVOPage, "ok");
    }

    @PostMapping("/list/page")
    @Operation(summary = "分页获取用户的点赞帖子列表", description = "根据指定的用户 ID 分页获取该用户点赞过的帖子列表")
    public BaseResponse<Page<PostVO>> listLikedPostByPage(@RequestBody PostLikeQueryRequest postLikeQueryRequest,
                                                          HttpServletRequest request) {
        if (postLikeQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long currentNum = postLikeQueryRequest.getCurrentNum();
        long pageSize = postLikeQueryRequest.getPageSize();
        Long userId = postLikeQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postLikeService.listLikedPostByPage(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postLikeQueryRequest.getPostQueryRequest()), userId);
        return new BaseResponse<>(0, postService.getPostVOPage(postPage, request), "ok");
    }
}
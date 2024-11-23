package com.zephyr.springboottemplate.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zephyr.springboottemplate.common.BaseResponse;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import com.zephyr.springboottemplate.model.dto.postcollection.PostCollectionAddRequest;
import com.zephyr.springboottemplate.model.dto.postcollection.PostCollectionQueryRequest;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.vo.PostVO;
import com.zephyr.springboottemplate.service.PostCollectionService;
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
@RequestMapping("/post_collection")
@Slf4j
@Tag(name = "帖子收藏管理", description = "帖子收藏相关操作接口")
public class PostCollectionController {

    @Resource
    private PostCollectionService postCollectionService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @PostMapping("/")
    @Operation(summary = "收藏帖子", description = "用户可以收藏指定的帖子，需要登录后操作")
    public BaseResponse<Integer> doPostCollection(@RequestBody PostCollectionAddRequest postCollectionAddRequest,
                                                  HttpServletRequest request) {
        if (postCollectionAddRequest == null || postCollectionAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long postId = postCollectionAddRequest.getPostId();
        int result = postCollectionService.doPostCollection(postId, loginUser);
        return new BaseResponse<>(0, result, "ok");
    }

    @PostMapping("/my/list/page")
    @Operation(summary = "分页获取我的收藏帖子列表", description = "登录用户可以分页获取自己收藏过的帖子列表")
    public BaseResponse<Page<PostVO>> listMyCollectedPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                                HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long currentNum = postQueryRequest.getCurrentNum();
        long pageSize = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postCollectionService.listCollectedPostByPage(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
        return new BaseResponse<>(0, postVOPage, "ok");
    }

    @PostMapping("/list/page")
    @Operation(summary = "分页获取指定用户的收藏帖子列表", description = "根据用户 ID 分页获取该用户收藏过的帖子列表")
    public BaseResponse<Page<PostVO>> listCollectedPostByPage(@RequestBody PostCollectionQueryRequest postCollectionQueryRequest,
                                                              HttpServletRequest request) {
        if (postCollectionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long currentNum = postCollectionQueryRequest.getCurrentNum();
        long pageSize = postCollectionQueryRequest.getPageSize();
        Long userId = postCollectionQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postCollectionService.listCollectedPostByPage(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postCollectionQueryRequest.getPostQueryRequest()), userId);
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
        return new BaseResponse<>(0, postVOPage, "ok");
    }
}
package com.zephyr.springboottemplate.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zephyr.springboottemplate.annotation.AuthCheck;
import com.zephyr.springboottemplate.common.BaseResponse;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.constant.UserConstant;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.model.dto.post.PostAddRequest;
import com.zephyr.springboottemplate.model.dto.post.PostEditRequest;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import com.zephyr.springboottemplate.model.dto.post.PostUpdateRequest;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.vo.PostVO;
import com.zephyr.springboottemplate.service.PostService;
import com.zephyr.springboottemplate.service.UserService;
import com.zephyr.springboottemplate.utils.ThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@Slf4j
@Tag(name = "帖子管理", description = "帖子相关操作接口")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    @Operation(summary = "添加帖子", description = "用户可以添加新的帖子，需传入帖子内容和标签")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest,
                                      HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        postService.validPost(post, true);
        User loginUser = userService.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setLikeCount(0);
        post.setCollectCount(0);
        boolean result = postService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = post.getId();
        return new BaseResponse<>(0, newPostId, "ok");
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "更新帖子", description = "管理员可以更新帖子内容")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean updateResult = postService.updateById(post);
        return new BaseResponse<>(0, updateResult, "ok");
    }

    @GetMapping("/get/vo")
    @Operation(summary = "获取帖子详情", description = "根据帖子 ID 获取帖子的详细信息，包括 VO 格式化后的内容")
    public BaseResponse<PostVO> getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        PostVO postVO = postService.getPostVO(post, request);
        return new BaseResponse<>(0, postVO, "ok");
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "分页获取帖子列表", description = "管理员可以根据条件分页获取帖子列表")
    public BaseResponse<Page<Post>> listPostByPage(@RequestBody PostQueryRequest postQueryRequest) {
        long currentNum = postQueryRequest.getCurrentNum();
        long pageSize = postQueryRequest.getPageSize();
        Page<Post> postPage = postService.page(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postQueryRequest));
        return new BaseResponse<>(0, postPage, "ok");
    }

    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取帖子 VO 列表", description = "分页获取帖子 VO 格式化后的列表，支持搜索过滤")
    public BaseResponse<Page<PostVO>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                       HttpServletRequest request) {
        long currentNum = postQueryRequest.getCurrentNum();
        long pageSize = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postQueryRequest));
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
        return new BaseResponse<>(0, postVOPage, "ok");
    }

    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取用户自己的帖子列表", description = "登录用户可以分页获取自己发布的帖子")
    public BaseResponse<Page<PostVO>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                         HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        long currentNum = postQueryRequest.getCurrentNum();
        long pageSize = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(currentNum, pageSize),
                postService.getQueryWrapper(postQueryRequest));
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
        return new BaseResponse<>(0, postVOPage, "ok");
    }

    @PostMapping("/search/page/vo")
    @Operation(summary = "分页搜索帖子", description = "支持通过关键词和其他条件搜索帖子，返回 VO 格式化后的分页数据")
    public BaseResponse<Page<PostVO>> searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                         HttpServletRequest request) {
        long pageSize = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
        return new BaseResponse<>(0, postVOPage, "ok");
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑帖子", description = "仅帖子作者或管理员可以编辑帖子")
    public BaseResponse<Boolean> editPost(@RequestBody PostEditRequest postEditRequest,
                                          HttpServletRequest request) {
        if (postEditRequest == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        User loginUser = userService.getLoginUser(request);
        long id = postEditRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或者管理员可以编辑
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean editResult = postService.updateById(post);
        return new BaseResponse<>(0, editResult, "ok");
    }
}

package com.zephyr.springboottemplate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.vo.PostVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 帖子服务
 */
public interface PostService extends IService<Post> {

        /**
        * 校验
        *
        * @param post 帖子对象
        * @param add 是否为新增操作
        */
        void validPost(Post post, boolean add);

        /**
        * 获取查询条件
        *
        * @param postQueryRequest 帖子查询请求对象
        * @return 查询条件构造器
        */
        QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

        /**
        * 从 ES 查询
        *
        * @param postQueryRequest 帖子查询请求对象
        * @return 帖子分页对象
        */
        Page<Post> searchFromEs(PostQueryRequest postQueryRequest);

        /**
        * 获取帖子封装
        *
        * @param post 帖子对象
        * @param request HTTP 请求对象
        * @return 帖子视图对象
        */
        PostVO getPostVO(Post post, HttpServletRequest request);

        /**
        * 分页获取帖子封装
        *
        * @param postPage 帖子分页对象
        * @param request HTTP 请求对象
        * @return 帖子视图对象分页对象
        */
        Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);
}

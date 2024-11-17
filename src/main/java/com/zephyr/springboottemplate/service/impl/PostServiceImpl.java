package com.zephyr.springboottemplate.service.impl;

import cn.hutool.core.collection.CollUtil;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.constant.SortConstant;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.mapper.PostCollectionMapper;
import com.zephyr.springboottemplate.mapper.PostLikeMapper;
import com.zephyr.springboottemplate.mapper.PostMapper;
import com.zephyr.springboottemplate.model.dto.post.PostEsDTO;
import com.zephyr.springboottemplate.model.dto.post.PostQueryRequest;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.model.entity.PostCollection;
import com.zephyr.springboottemplate.model.entity.PostLike;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.vo.PostVO;
import com.zephyr.springboottemplate.model.vo.UserVO;
import com.zephyr.springboottemplate.service.PostService;
import com.zephyr.springboottemplate.service.UserService;
import com.zephyr.springboottemplate.utils.SqlUtils;
import com.zephyr.springboottemplate.utils.ThrowUtils;
import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private UserService userService;

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private PostCollectionMapper postCollectionMapper;

    @Resource
    private ElasticsearchOperations elasticsearchOperations;
    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();

        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }

        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }

        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }

        Long id = postQueryRequest.getId();
        Long notId = postQueryRequest.getNotId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tags = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();

        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);

        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(
                SqlUtils.validSortField(sortField),             // 1. 是否启用排序条件
                sortOrder.equals(SortConstant.SORT_ORDER_ASC), // 2. 排序方向（升序或降序）
                sortField                                      // 3. 排序字段
        );

        return queryWrapper;
    }

    @Override
    public Page<Post> searchFromEs(PostQueryRequest postQueryRequest) {
        // 从请求参数中提取条件
        Long id = postQueryRequest.getId();                 // 查询特定 ID 的记录
        Long notId = postQueryRequest.getNotId();           // 排除特定 ID 的记录
        String searchText = postQueryRequest.getSearchText(); // 按关键字搜索
        String title = postQueryRequest.getTitle();         // 按标题搜索
        String content = postQueryRequest.getContent();     // 按内容搜索
        List<String> tags = postQueryRequest.getTags();     // 必须包含的标签
        List<String> orTags = postQueryRequest.getOrTags(); // 可选包含的标签
        Long userId = postQueryRequest.getUserId();         // 按用户 ID 过滤
        String sortField = postQueryRequest.getSortField(); // 排序字段
        String sortOrder = postQueryRequest.getSortOrder(); // 排序方式（升序/降序）
        long currentNum = postQueryRequest.getCurrentNum() - 1; // 当前页码（Elasticsearch 起始页为 0）
        long pageSize = postQueryRequest.getPageSize();     // 每页大小

        // 构建 BoolQuery（布尔查询）
        Query boolQuery = QueryBuilders.bool(b -> {
            // 添加过滤条件
            b.filter(QueryBuilders.term(t -> t.field("isDelete").value(0))); // 仅查询未删除的数据

            if (id != null) {
                b.filter(QueryBuilders.term(t -> t.field("id").value(id))); // 按 ID 精确匹配
            }
            if (notId != null) {
                b.mustNot(QueryBuilders.term(t -> t.field("id").value(notId))); // 排除特定 ID
            }
            if (userId != null) {
                b.filter(QueryBuilders.term(t -> t.field("userId").value(userId))); // 按用户 ID 精确匹配
            }

            // 必须包含所有指定标签
            if (CollUtil.isNotEmpty(tags)) {
                tags.forEach(tag -> b.filter(QueryBuilders.term(t -> t.field("tags").value(tag))));
            }

            // 包含任意一个标签即可
            if (CollUtil.isNotEmpty(orTags)) {
                b.should(orTags.stream()
                        .map(tag -> QueryBuilders.term(t -> t.field("tags").value(tag)))
                        .toList());
            }

            // 按关键词搜索（标题、描述、内容）
            if (StringUtils.isNotBlank(searchText)) {
                b.should(QueryBuilders.match(m -> m.field("title").query(searchText))); // 标题匹配
                b.should(QueryBuilders.match(m -> m.field("description").query(searchText))); // 描述匹配
                b.should(QueryBuilders.match(m -> m.field("content").query(searchText))); // 内容匹配
            }

            // 按标题精确搜索
            if (StringUtils.isNotBlank(title)) {
                b.should(QueryBuilders.match(m -> m.field("title").query(title)));
            }

            // 按内容精确搜索
            if (StringUtils.isNotBlank(content)) {
                b.should(QueryBuilders.match(m -> m.field("content").query(content)));
            }

            return b; // 返回 BoolQuery.Builder
        });

        // 排序
        SortOptions sortOptions = SortOptions.of(s -> s
                .score(s1 -> s1) // 默认按相关性排序
        );

        // 如果指定了排序字段，则按字段排序
        if (StringUtils.isNotBlank(sortField)) {
            sortOptions = SortOptions.of(s -> s
                    .field(f -> f
                            .field(sortField) // 排序字段
                            .order("ASC".equalsIgnoreCase(sortOrder) ? SortOrder.Asc : SortOrder.Desc) // 排序顺序
                    ));
        }

        // 分页参数
        PageRequest pageRequest = PageRequest.of((int) currentNum, (int) pageSize);

        // 构建查询
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)         // 设置查询条件
                .withPageable(pageRequest)    // 设置分页参数
                .withSort(sortOptions)        // 设置排序规则
                .build();

        // 执行查询
        SearchHits<PostEsDTO> searchHits = elasticsearchOperations.search(nativeQuery, PostEsDTO.class);

        // 初始化返回的分页对象
        Page<Post> page = new Page<>();
        page.setTotal(searchHits.getTotalHits()); // 设置总记录数
        List<Post> resourceList = new ArrayList<>();

        // 查出结果后，从数据库获取动态数据（如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<PostEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> postIdList = searchHitList.stream()
                    .map(searchHit -> searchHit.getContent().getId())
                    .toList();

            // 批量从数据库查询最新数据
            List<Post> postList = baseMapper.selectBatchIds(postIdList);
            if (postList != null) {
                Map<Long, List<Post>> idPostMap = postList.stream()
                        .collect(Collectors.groupingBy(Post::getId));

                // 遍历查询结果，合并 ES 和数据库的数据
                postIdList.forEach(postId -> {
                    if (idPostMap.containsKey(postId)) {
                        resourceList.add(idPostMap.get(postId).get(0)); // 添加最新数据
                    } else {
                        // 清除已被物理删除的数据
                        String delete = elasticsearchOperations.delete(String.valueOf(postId), PostEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }

        page.setRecords(resourceList); // 设置分页记录
        return page; // 返回分页结果
    }

    @Override
    public PostVO getPostVO(Post post, HttpServletRequest request) {
        // 将 Post 转换为 PostVO（DTO -> VO）
        PostVO postVO = PostVO.objToVo(post);
        long postId = post.getId();

        // 1. 关联用户信息
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            // 根据用户 ID 查询用户信息
            user = userService.getById(userId);
        }
        // 将用户信息封装为 UserVO
        UserVO userVO = userService.getUserVO(user);
        postVO.setUser(userVO);

        // 2. 检查当前是否已登录
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取用户是否点赞了该帖子
            QueryWrapper<PostLike> postLikeQueryWrapper = new QueryWrapper<>();
            postLikeQueryWrapper.in("postId", postId); // 过滤条件：帖子 ID
            postLikeQueryWrapper.eq("userId", loginUser.getId()); // 过滤条件：用户 ID
            PostLike postLike = postLikeMapper.selectOne(postLikeQueryWrapper);
            postVO.setHasLike(postLike != null); // 设置是否点赞

            // 获取用户是否收藏了该帖子
            QueryWrapper<PostCollection> postCollectionQueryWrapper = new QueryWrapper<>();
            postCollectionQueryWrapper.in("postId", postId); // 过滤条件：帖子 ID
            postCollectionQueryWrapper.eq("userId", loginUser.getId()); // 过滤条件：用户 ID
            PostCollection postCollection = postCollectionMapper.selectOne(postCollectionQueryWrapper);
            postVO.setHasCollect(postCollection != null); // 设置是否收藏
        }
        return postVO; // 返回封装的 PostVO
    }

    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        // 获取当前页的 Post 列表
        List<Post> postList = postPage.getRecords();
        // 初始化返回的 PostVO 分页对象
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());

        if (CollUtil.isEmpty(postList)) {
            return postVOPage; // 如果没有帖子，直接返回空分页
        }

        // 1. 批量查询关联的用户信息
        Set<Long> userIdSet = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet) // 根据用户 ID 批量查询用户信息
                .stream()
                .collect(Collectors.groupingBy(User::getId)); // 按用户 ID 分组

        // 2. 检查当前是否已登录，并获取点赞、收藏状态
        Map<Long, Boolean> postIdHasLikeMap = new HashMap<>();
        Map<Long, Boolean> postIdHasCollectMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);

            // 批量查询用户对所有帖子是否点赞
            QueryWrapper<PostLike> postLikeQueryWrapper = new QueryWrapper<>();
            postLikeQueryWrapper.in("postId", postIdSet); // 过滤条件：帖子 ID
            postLikeQueryWrapper.eq("userId", loginUser.getId()); // 过滤条件：用户 ID
            List<PostLike> postLikeList = postLikeMapper.selectList(postLikeQueryWrapper);
            postLikeList.forEach(postLike -> postIdHasLikeMap.put(postLike.getPostId(), true)); // 标记为已点赞

            // 批量查询用户对所有帖子是否收藏
            QueryWrapper<PostCollection> postCollectionQueryWrapper = new QueryWrapper<>();
            postCollectionQueryWrapper.in("postId", postIdSet); // 过滤条件：帖子 ID
            postCollectionQueryWrapper.eq("userId", loginUser.getId()); // 过滤条件：用户 ID
            List<PostCollection> postCollectionList = postCollectionMapper.selectList(postCollectionQueryWrapper);
            postCollectionList.forEach(postCollection -> postIdHasCollectMap.put(postCollection.getPostId(), true)); // 标记为已收藏
        }

        // 3. 填充信息，转换为 PostVO
        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = PostVO.objToVo(post); // 转换为 PostVO
            Long userId = post.getUserId();
            User user = null;

            // 设置关联的用户信息
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            postVO.setUser(userService.getUserVO(user));

            // 设置点赞和收藏状态
            postVO.setHasLike(postIdHasLikeMap.getOrDefault(post.getId(), false));
            postVO.setHasCollect(postIdHasCollectMap.getOrDefault(post.getId(), false));
            return postVO;
        }).toList();

        postVOPage.setRecords(postVOList); // 设置分页记录
        return postVOPage; // 返回封装的分页对象
    }
}

package com.zephyr.springboottemplate.job.once;


import java.util.List;
import java.util.stream.Collectors;

import com.zephyr.springboottemplate.esdao.PostEsDao;
import com.zephyr.springboottemplate.model.dto.post.PostEsDTO;
import com.zephyr.springboottemplate.model.entity.Post;
import com.zephyr.springboottemplate.service.PostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.springframework.boot.CommandLineRunner;

/**
 * 全量同步帖子到 Elasticsearch (ES)
 *
 * 功能描述：
 * 1. 在系统启动时运行（需要启用 @Component 注解）。
 * 2. 从数据库中获取所有帖子数据。
 * 3. 将帖子数据转换为 Elasticsearch 的数据传输对象 (DTO) 格式。
 * 4. 分页批量同步数据到 Elasticsearch。
 * 5. 记录日志，便于监控任务执行情况。
 * <p>
 * 使用场景：
 * 1. 初始化搜索索引：首次部署搜索系统时，将数据库中的数据加载到 Elasticsearch。
 * 2. 索引修复：当 Elasticsearch 数据损坏或丢失时重新同步。
 * <p>
 * 注意：
 * 1. 当前为一次性任务（类名中的 "once" 表示），需手动启用。
 * 2. 批量同步的分页大小默认为 500，可根据数据量调整。
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    // 注入服务层，用于从数据库中查询帖子数据
    @Resource
    private PostService postService;

    // 注入 Elasticsearch 的 DAO 层，用于将数据保存到 Elasticsearch
    @Resource
    private PostEsDao postEsDao;

    /**
     * 任务执行方法，系统启动时触发。
     *
     * @param args 启动参数（未使用）
     */
    @Override
    public void run(String... args) {
        // 从数据库中查询所有帖子数据
        List<Post> postList = postService.list();

        // 如果帖子数据为空，直接返回，避免执行后续操作
        if (CollUtil.isEmpty(postList)) {
            return;
        }

        // 将帖子实体数据转换为 Elasticsearch 数据传输对象 (DTO)
        List<PostEsDTO> postEsDTOList = postList.stream()
                .map(PostEsDTO::objToDto) // 调用静态方法将实体转换为 DTO
                .collect(Collectors.toList());

        // 定义分页大小，避免一次性加载过多数据导致内存溢出
        final int pageSize = 500;
        int total = postEsDTOList.size();

        // 记录任务开始日志
        log.info("FullSyncPostToEs start, total {}", total);

        // 分页处理数据
        for (int i = 0; i < total; i += pageSize) {
            // 计算当前分页的结束索引
            int end = Math.min(i + pageSize, total);

            // 记录当前同步的范围
            log.info("sync from {} to {}", i, end);

            // 将当前分页的数据保存到 Elasticsearch
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }

        // 记录任务结束日志
        log.info("FullSyncPostToEs end, total {}", total);
    }
}
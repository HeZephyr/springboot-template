package com.zephyr.springboottemplate.job.cycle;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.zephyr.springboottemplate.esdao.PostEsDao;
import com.zephyr.springboottemplate.mapper.PostMapper;
import com.zephyr.springboottemplate.model.dto.post.PostEsDTO;
import com.zephyr.springboottemplate.model.entity.Post;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 增量同步帖子到 Elasticsearch (ES)
 *
 * 功能描述：
 * 1. 定时任务，每分钟执行一次。
 * 2. 查询过去 5 分钟内新增或更新的帖子数据。
 * 3. 将帖子数据转换为 Elasticsearch 的数据传输对象 (DTO) 格式。
 * 4. 分页批量同步到 Elasticsearch。
 * 5. 记录任务日志，便于监控和排查问题。
 * <p>
 * 使用场景：
 * 1. 增量同步：在运行中的系统中实时保持数据库与 Elasticsearch 数据的一致性。
 * 2. 定时更新索引：确保搜索服务的数据与实际数据库保持同步。
 * <p>
 * 注意：
 * 1. 当前任务采用定时触发方式，默认每分钟执行一次。
 * 2. 数据量较大时采用分页方式同步，避免内存溢出。
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncPostToEs {

    // 注入 Mapper 层，用于查询数据库中帖子数据
    @Resource
    private PostMapper postMapper;

    // 注入 Elasticsearch 的 DAO 层，用于将数据保存到 Elasticsearch
    @Resource
    private PostEsDao postEsDao;

    /**
     * 定时任务：每分钟执行一次，增量同步数据到 Elasticsearch。
     *
     * 查询规则：
     * 1. 查找最近 5 分钟内新增或修改的帖子数据，包括已删除数据（用于更新索引或删除操作）。
     * 2. 数据为空时跳过同步。
     * 3. 分页保存到 Elasticsearch。
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 计算 5 分钟前的时间点
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);

        // 查询过去 5 分钟内新增、修改或删除的帖子数据
        List<Post> postList = postMapper.listPostWithDelete(fiveMinutesAgoDate);

        // 如果没有增量数据，记录日志并退出
        if (CollUtil.isEmpty(postList)) {
            log.info("no inc post");
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
        log.info("IncSyncPostToEs start, total {}", total);

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
        log.info("IncSyncPostToEs end, total {}", total);
    }
}
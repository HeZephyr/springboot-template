package com.zephyr.springboottemplate.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.zephyr.springboottemplate.config.CosClientConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 腾讯云 COS（对象存储）管理器
 *
 * <p>
 * 提供文件上传功能，通过封装腾讯云 COS 客户端的操作实现。
 * 支持基于文件路径或 File 对象的文件上传。
 * </p>
 */
@Component
public class CosManager {

    /**
     * 配置信息（如存储桶名称等），由 {@link CosClientConfig} 提供。
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 腾讯云 COS 客户端，用于与腾讯云对象存储服务交互。
     */
    @Resource
    private COSClient cosClient;

    /**
     * 上传文件到腾讯云 COS（基于文件路径）
     *
     * @param key           文件在存储桶中的对象键（路径或文件名，必须唯一）
     * @param localFilePath 本地文件路径
     * @return {@link PutObjectResult} 上传结果
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        // 创建上传请求，指定存储桶、对象键和本地文件路径
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                cosClientConfig.getBucket(), key, new File(localFilePath));
        // 上传文件
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到腾讯云 COS（基于 File 对象）
     *
     * @param key  文件在存储桶中的对象键（路径或文件名，必须唯一）
     * @param file 文件对象
     * @return {@link PutObjectResult} 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        // 创建上传请求，指定存储桶、对象键和文件对象
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                cosClientConfig.getBucket(), key, file);
        // 上传文件
        return cosClient.putObject(putObjectRequest);
    }
}
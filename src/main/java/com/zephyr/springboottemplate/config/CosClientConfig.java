package com.zephyr.springboottemplate.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云 COS（对象存储）客户端配置类
 *
 * <p>
 * 用于初始化和配置腾讯云 COS 的客户端，支持文件上传、下载等操作。
 * 配置从 application.yml 或 application.properties 文件中加载，需定义对应的 cos.client 前缀的属性。
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    /**
     * 访问密钥 Access Key，用于身份认证
     * 需从腾讯云控制台获取。
     */
    private String accessKey;

    /**
     * 访问密钥 Secret Key，与 Access Key 搭配使用，用于签名请求。
     */
    private String secretKey;

    /**
     * 区域信息，指定 COS 服务所在的数据中心
     * 可参考腾讯云文档获取地域的简称，如 ap-guangzhou 表示广州区域。
     */
    private String region;

    /**
     * 存储桶名称，用于唯一标识文件存储的逻辑容器
     * 在使用 COS 服务时，需指定存储桶名称。
     */
    private String bucket;

    /**
     * 配置 COS 客户端 Bean
     *
     * @return COSClient 实例，用于与腾讯云对象存储服务交互
     */
    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息 (AccessKey 和 SecretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);

        // 设置存储桶的区域，需根据项目实际需求填写区域信息
        // 可参考文档：https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(region));

        // 返回 COS 客户端实例，用于后续操作
        return new COSClient(cred, clientConfig);
    }
}
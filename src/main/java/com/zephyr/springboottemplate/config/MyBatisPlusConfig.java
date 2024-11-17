package com.zephyr.springboottemplate.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置 MyBatis-Plus 拦截器以及其他相关设置。
 */
@Configuration
@MapperScan("com.zephyr.springboottemplate.mapper") // 扫描 Mapper 接口所在的包，自动注册到 Spring 容器中
public class MyBatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 插件拦截器
     *
     * MyBatis-PlusInterceptor 是 MyBatis-Plus 的核心插件拦截器，用于扩展 MyBatis 的功能。
     * 这里主要添加分页拦截器以支持分页查询。
     *
     * @return 配置好的 MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器，指定数据库类型为 MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
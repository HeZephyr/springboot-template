package com.zephyr.springboottemplate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC JSON 配置类
 * <p>
 * 主要用于解决 JSON 转换中 Long 类型数据精度丢失的问题，将 Long 类型的数据序列化为字符串。
 * </p>
 */
@Configuration
public class JsonConfig {

    /**
     * 定制化 ObjectMapper Bean
     *
     * <p>
     * 将 Long 类型和 long 基本类型的字段，在序列化为 JSON 时转化为字符串，避免前端处理 Long 类型时精度丢失。
     * </p>
     *
     * @param builder Jackson2ObjectMapperBuilder，用于构建定制化的 ObjectMapper
     * @return 定制化的 ObjectMapper 实例
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 使用 builder 创建一个 ObjectMapper 实例
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 创建一个 Jackson 的 SimpleModule，用于注册自定义序列化器
        SimpleModule module = new SimpleModule();

        // 为 Long 和 long 类型添加序列化器，将其转换为字符串
        module.addSerializer(Long.class, ToStringSerializer.instance); // 对象类型 Long
        module.addSerializer(Long.TYPE, ToStringSerializer.instance); // 基本类型 long

        // 将自定义模块注册到 ObjectMapper 中
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
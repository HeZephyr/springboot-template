package com.zephyr.springboottemplate.utils;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文获取工具
 * <p>
 * 功能描述：
 * 1. 提供获取 Spring 容器中 Bean 的静态方法。
 * 2. 适用于非 Spring 托管类中需要访问 Spring Bean 的场景。
 * </p>
 * 注意事项：
 * 1. 尽量优先使用依赖注入（如 @Autowired、@Resource）获取 Bean，避免滥用该工具。
 * 2. 工具类只能在 Spring 容器初始化完成后使用，否则可能导致空指针异常。
 * <p>
 * 使用场景：
 * 1. 工具类或第三方库需要访问 Spring 容器中的 Bean。
 * 2. 动态按名称、类型获取 Bean。
 * </p>
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    /**
     * 静态变量，用于保存 Spring 应用上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置 Spring 应用上下文。
     * <p>
     * Spring 容器初始化时会自动调用此方法，将上下文传入。
     *
     * @param applicationContext Spring 应用上下文对象
     * @throws BeansException 如果设置失败，会抛出此异常
     */
    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        // 保存 Spring 上下文到静态变量中，供后续获取 Bean 使用
        SpringContextUtils.applicationContext = applicationContext;
    }

    /**
     * 根据 Bean 名称获取 Bean 实例。
     * <p>
     * 使用场景：
     * 1. 当 Bean 名称是动态生成或运行时传入时，可以使用此方法。
     * </p>
     * @param beanName Bean 的名称
     * @return Bean 的实例对象
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据 Bean 类型获取 Bean 实例。
     *
     * 使用场景：
     * 1. 当容器中只有一个该类型的 Bean 时，可直接使用此方法。
     *
     * @param beanClass Bean 的类型
     * @param <T> Bean 的具体类型
     * @return Bean 的实例对象
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    /**
     * 根据 Bean 名称和类型获取 Bean 实例。
     * <p>
     * 使用场景：
     * 1. 当容器中存在多个相同类型的 Bean 时，需要同时指定名称和类型。
     *
     * @param beanName Bean 的名称
     * @param beanClass Bean 的类型
     * @param <T> Bean 的具体类型
     * @return Bean 的实例对象
     */
    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return applicationContext.getBean(beanName, beanClass);
    }
}
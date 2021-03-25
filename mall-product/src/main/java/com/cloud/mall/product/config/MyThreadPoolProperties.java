package com.cloud.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author ws
 * @Date 2021/3/17 22:18
 * @Version 1.0
 */

/**
 * MyThreadPool的自定义线程池的属性配置
 */
@ConfigurationProperties(prefix = "product.threadpool")
@Data
public class MyThreadPoolProperties {
    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;
    /**
     * 最大空闲时间
     */
    private Integer keepAliveTime;
}

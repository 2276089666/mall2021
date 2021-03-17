package com.cloud.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author ws
 * @Date 2021/3/17 22:18
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "product.threadpool")
@Data
public class MyThreadPoolProperties {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;
}

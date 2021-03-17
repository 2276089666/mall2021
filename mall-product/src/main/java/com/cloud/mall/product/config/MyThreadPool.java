package com.cloud.mall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @Author ws
 * @Date 2021/3/17 22:13
 * @Version 1.0
 */
@EnableConfigurationProperties(MyThreadPoolProperties.class)
@Configuration
public class MyThreadPool {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(MyThreadPoolProperties myThreadPoolProperties) {
        return new ThreadPoolExecutor(myThreadPoolProperties.getCorePoolSize(), myThreadPoolProperties.getMaximumPoolSize(), myThreadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }
}

package com.cloud.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author ws
 * @Date 2021/3/7 16:15
 * @Version 1.0
 */
@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        //可以用"rediss://"来启用SSL连接
        config.useSingleServer().setAddress("redis://101.201.124.233:6379");
        return Redisson.create(config);
    }
}

package com.cloud.mall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author ws
 * @Date 2021/3/9 18:35
 * @Version 1.0
 */
@Configuration
//开启缓存抽象
/**
 * 1.默认自动生成key category::SimpleKey []
 * 2.默认使用jdk序列化机制，将序列化后的数据存到redis中
 * 3.默认过期时间 TTL:-1 永不过期
 *
 * 原理：CacheAutoConfiguration-->CacheConfigurations-->RedisCacheConfiguration-->配置了RedisCacheManager(初始化所有缓存，每个缓存用什么配置)
 *     -->getIfAvailable（看RedisCacheConfiguration是否可用，没有就用默认的）-->想修改redis缓存的配置-->给容器中添加一个RedisCacheConfiguration
 *     -->RedisCacheManager会读取RedisCacheConfiguration管理所有的缓存分区（我们给的缓存名默认就是一个区）
 */
/**
 *  spring-cache的不足：
 *      读模式：
 *          缓存穿透：cache-null-values: true
 *          缓存击穿：默认无锁 ，可以使用sync = true 加了synchronized本地锁，其实有100同模块的微服务最多也就放100请求进来，基本上解决了问题
 *          缓存雪崩：加上过期时间 time-to-live,其实加随机时间有时候会弄巧成拙导致大量key同时过期
 *      写模式：（数据库与缓存一致）spring-cache没管，让我们自己处理
 *          1.加读写锁
 *          2.引入canal,感知数据库的变化去更新缓存
 *          3.读多写多,直接去数据库不用缓存
 *
 *    总结：常规数据用spring-cache，特殊数据特殊设计
 *
 *
 *
 */
@EnableCaching
public class MyCacheConfig {

    /**
     * package org.springframework.boot.autoconfigure.cache;
     * RedisCacheConfiguration
     * 人家是这么配置的
     * @param cacheProperties
     * @return
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        //由于方法的返回值还是RedisCacheConfiguration我们要把cacheConfig=method()
        //key
        config=config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        //value
        config=config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));

        /**
         * 我们的配置文件配置的缓存过期时间失效，查看源码发现RedisCacheConfiguration里面可以抄
         *
         */
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}

package com.cloud.mall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author ws
 * @Date 2021/1/9 16:40
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cloud.mall.ware.dao")
public class MallWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallWareApplication.class,args);
    }
}

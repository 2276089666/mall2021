package com.cloud.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author ws
 * @Date 2021/1/9 13:27
 * @Version 1.0
 */
//配置我们的dao下的mapper的路径
@MapperScan("com.cloud.mall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cloud.mall.product.feign")
public class MallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallProductApplication.class,args);
    }
}

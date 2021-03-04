package com.cloud.mall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author ws
 * @Date 2021/1/9 16:25
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.cloud.mall.member.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cloud.mall.member.feign")
public class MallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallMemberApplication.class,args);
    }
}

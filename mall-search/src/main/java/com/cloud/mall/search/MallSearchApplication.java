package com.cloud.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author ws
 * @Date 2021/2/28 15:24
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallSearchApplication.class,args);
    }
}

package com.cloud.mall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author ws
 * @Date 2021/3/18 12:53
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallAuthserverApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAuthserverApplication.class,args);
    }
}

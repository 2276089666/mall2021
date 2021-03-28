package com.cloud.mall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author ws
 * @Date 2021/3/18 12:53
 * @Version 1.0
 */
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class MallAuthserverApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAuthserverApplication.class,args);
    }
}

package com.cloud.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author ws
 * @Date 2021/1/10 13:18
 * @Version 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MallGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallGateWayApplication.class,args);
    }
}

package com.cloud.mall.thirdParty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author ws
 * @Date 2021/1/23 16:49
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallThirdPartyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallThirdPartyApplication.class,args);
    }
}

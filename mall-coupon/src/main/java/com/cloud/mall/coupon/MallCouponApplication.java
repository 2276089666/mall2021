package com.cloud.mall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @Author ws
 * @Date 2021/1/9 16:07
 * @Version 1.0
 */
@MapperScan("com.cloud.mall.coupon.dao")
@EnableFeignClients(basePackages = "com.cloud.mall.coupon.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class MallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallCouponApplication.class,args);
    }
}

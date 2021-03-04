package com.cloud.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author ws
 * @Date 2021/1/9 16:33
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.cloud.mall.order.dao")
public class MallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class,args);
    }
}

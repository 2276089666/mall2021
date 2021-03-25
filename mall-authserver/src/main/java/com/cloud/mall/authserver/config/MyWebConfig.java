package com.cloud.mall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author ws
 * @Date 2021/3/18 15:21
 * @Version 1.0
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    /**
     * 视图映射
     *     @GetMapping(path = {"login.html","login"})
     *     public String loginPage(){
     *         return "login";
     *     }
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}

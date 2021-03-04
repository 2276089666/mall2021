package com.cloud.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Author ws
 * @Date 2021/1/10 17:31
 * @Version 1.0
 */
@Configuration
public class CorsConfig {

    /**
     * 解决跨域
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        /**
         * 配置跨域
         */
        //请求头
        corsConfiguration.addAllowedHeader("*");
        //请求方式GET POST等
        corsConfiguration.addAllowedMethod("*");
        //跨域服务器
        corsConfiguration.addAllowedOrigin("*");
        //是否允许cookie
        corsConfiguration.setAllowCredentials(true);
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}

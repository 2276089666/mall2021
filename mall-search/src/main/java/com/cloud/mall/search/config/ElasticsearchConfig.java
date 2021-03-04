package com.cloud.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author ws
 * @Date 2021/2/28 15:27
 * @Version 1.0
 */
@Configuration
public class ElasticsearchConfig {

    /**
     * RequestOptions
     */
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 官方文档Java High Level REST Client标题下的Getting started下的Initialization
     * @return
     */
    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("159.75.22.98", 9200, "http")
//                        如果有多个节点时传入多个new HttpHost("localhost", 9201, "http")
                        ));
        return client;
    }
}

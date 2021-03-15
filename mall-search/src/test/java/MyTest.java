import com.alibaba.fastjson.JSON;
import com.cloud.mall.search.MallSearchApplication;
import com.cloud.mall.search.config.ElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

/**
 * @Author ws
 * @Date 2021/2/28 15:40
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallSearchApplication.class)
public class MyTest {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void connect(){
        System.out.println(restHighLevelClient);
    }

    @Test
    public void IndexAPI() throws IOException {
        IndexRequest request = new IndexRequest("user");
        User user = new User();
        user.setUserName("胡康");
        user.setPassword("250");
        String string = JSON.toJSONString(user);
        request.source(string, XContentType.JSON);
        IndexResponse createIndexResponse = restHighLevelClient.index(request, ElasticsearchConfig.COMMON_OPTIONS);
        System.out.println(createIndexResponse);
    }

    @Data
    class User{
        private String userName;
        private String password;
    }

    @Test
    public void searchData() throws IOException {
        /**
         * elk的 DSL
         *
         * GET bank/_search
         * {
         *   "query": {
         *     "match": {
         *       "address": "mill"
         *     }
         *   },
         *   "aggs": {
         *     "ageAgg": {
         *       "terms": {
         *         "field": "age",
         *         "size": 10
         *       }
         *     },
         *     "balanceAgg": {
         *       "avg": {
         *         "field": "balance"
         *       }
         *     }
         *   }
         * }
         */
        SearchRequest searchRequest = new SearchRequest();
        //查哪个索引
        searchRequest.indices("bank");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //查询条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        //在查询结果下的第一个聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        //在查询的结果下的第二个聚合
        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("balanceAgg").field("balance");
        searchSourceBuilder.aggregation(avgAggregationBuilder);

        searchRequest.source(searchSourceBuilder);
        //执行查询得到响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);

        //拿到查询条件的查询数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit searchHit : hits1) {
            System.out.println("index:\t"+searchHit.getIndex());
            System.out.println("id:\t"+searchHit.getId());
            String sourceAsString = searchHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account:\t"+account);
        }

        //拿到第一个聚合的数据
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        List<? extends Terms.Bucket> buckets = ageAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println("key:\t"+bucket.getKeyAsString());
            System.out.println("count:\t"+bucket.getDocCount());
        }

        //拿到第二个聚合的数据
        Avg balanceAgg = aggregations.get("balanceAgg");
        System.out.println("avg:\t"+balanceAgg.getValue());
    }

    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    public void TestMathCeil(){
        int ceil = (int) Math.ceil(20L / (int) 15);
        System.out.println(ceil);
    }
}

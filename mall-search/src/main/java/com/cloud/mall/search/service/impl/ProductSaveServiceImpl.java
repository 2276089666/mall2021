package com.cloud.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.cloud.common.to.es.SkuEsModel;
import com.cloud.mall.search.config.ElasticsearchConfig;
import com.cloud.mall.search.constant.EsConstant;
import com.cloud.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ws
 * @Date 2021/3/2 19:43
 * @Version 1.0
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> list) throws IOException {

//        1.建立索引和映射关系DSL
//        PUT product
//        {
//            "mappings": {
//            "properties": {
//                "skuId": {
//                    "type": "long"
//                },
//                "spuId": {
//                    "type": "keyword"
//                },
//                "skuTitle": {
//                    "type": "text",
//                            "analyzer": "ik_smart"
//                },
//                "skuPrice": {
//                    "type": "keyword"
//                },
//                "skuImg": {
//                    "type": "keyword"
//                },
//                "saleCount": {
//                    "type": "long"
//                },
//                "hasStock": {
//                    "type": "boolean"
//                },
//                "hotScore": {
//                    "type": "long"
//                },
//                "brandId": {
//                    "type": "long"
//                },
//                "catalogId": {
//                    "type": "long"
//                },
//                "brandName": {
//                    "type": "keyword"
//                },
//                "brandImg": {
//                    "type": "keyword"
//                },
//                "catalogName": {
//                    "type": "keyword"
//                },
//                "attrs": {
//                    "type": "nested",
//                            "properties": {
//                        "attrId": {
//                            "type": "long"
//                        },
//                        "attrName": {
//                            "type": "keyword"
//                        },
//                        "attrValue": {
//                            "type": "keyword"
//                        }
//                    }
//                }
//            }
//        }
//        }

//        2.批量保存
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : list) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String skuEsModelString = JSON.toJSONString(skuEsModel);
            indexRequest.source(skuEsModelString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();
        if (b){
            List<Long> collect = list.stream().map(SkuEsModel::getSkuId).collect(Collectors.toList());
            log.error("es保存出现错误,商品的skuId:{}",collect);
        }
        return b;
    }
}

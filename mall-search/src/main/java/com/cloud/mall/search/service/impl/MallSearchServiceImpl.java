package com.cloud.mall.search.service.impl;

import com.cloud.mall.search.service.MallSearchService;
import com.cloud.mall.search.vo.SearchParamVo;
import com.cloud.mall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author ws
 * @Date 2021/3/11 19:44
 * @Version 1.0
 */
@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {


    //商品检索DSL
//    GET product/_search
//    {
//        "query": {
//        "bool": {
//            "must": [
//            {
//                "match": {
//                "skuTitle": "华为"
//            }
//            }
//      ],
//            "filter": [
//            {
//                "term": {
//                "catalogId": "225"
//            }
//            },
//            {
//                "terms": {
//                "brandId": [
//                "1",
//                        "2",
//                        "9"
//            ]
//            }
//            },
//            {
//                "nested": {
//                "path": "attrs",
//                        "query": {
//                    "bool": {
//                        "must": [
//                        {
//                            "term": {
//                            "attrs.attrId": {
//                                "value": "15"
//                            }
//                        }
//                        },
//                        {
//                            "terms": {
//                            "attrs.attrValue": [
//                            "2020年",
//                                    "2021年"
//                      ]
//                        }
//                        }
//                ]
//                    }
//                }
//            }
//            },
//            {
//                "term": {
//                "hasStock": "true"
//            }
//            },
//            {
//                "range": {
//                "skuPrice": {
//                    "gte": 0,
//                            "lte": 6000
//                }
//            }
//            }
//      ]
//        }
//    },
//        "sort": [
//        {
//            "skuPrice": {
//            "order": "desc"
//        }
//        }
//  ],
//        "from": 0,
//            "size": 5,
//            "highlight": {
//        "fields": {
//            "skuTitle": {}
//        },
//        "pre_tags": "<b style='color:red'>",
//                "post_tags": "</b>"
//    },
//        "aggs": {
//        "brand_agg": {
//            "terms": {
//                "field": "brandId",
//                        "size": 10
//            },
//            "aggs": {
//                "brand_name_agg": {
//                    "terms": {
//                        "field": "brandName",
//                                "size": 10
//                    }
//                },
//                "brand_img_agg": {
//                    "terms": {
//                        "field": "brandImg",
//                                "size": 10
//                    }
//                }
//            }
//        },
//        "catalog_agg": {
//            "terms": {
//                "field": "catalogId",
//                        "size": 10
//            },
//            "aggs": {
//                "catalog_name_agg": {
//                    "terms": {
//                        "field": "catalogName",
//                                "size": 10
//                    }
//                }
//            }
//        },
//        "attr_agg": {
//            "nested": {
//                "path": "attrs"
//            },
//            "aggs": {
//                "attr_id_agg": {
//                    "terms": {
//                        "field": "attrs.attrId",
//                                "size": 10
//                    },
//                    "aggs": {
//                        "attr_name_agg": {
//                            "terms": {
//                                "field": "attrs.attrName",
//                                        "size": 10
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//    }


    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        return null;
    }
}

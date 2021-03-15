package com.cloud.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/1 19:21
 * @Version 1.0
 */
@Data
public class SkuEsModel {

    //product的mapping:  productMappingDSL.json


    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<Attrs> attrs;

    @Data
    public static class Attrs{

        private Long attrId;

        private String attrName;

        private String attrValue;
    }
}

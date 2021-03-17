package com.cloud.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/16 17:12
 * @Version 1.0
 */
@Data
public class SkuItemSaleAttr {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValue;
}

package com.cloud.common.to;

import lombok.Data;

/**
 * @Author ws
 * @Date 2021/3/2 16:08
 * @Version 1.0
 */
@Data
public class SkuHasStockTo {

    private Long skuId;

    private Integer stock;
}

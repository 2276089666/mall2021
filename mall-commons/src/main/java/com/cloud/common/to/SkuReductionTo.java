package com.cloud.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author ws
 * @Date 2021/2/19 17:43
 * @Version 1.0
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}

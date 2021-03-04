package com.cloud.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ws
 * @Date 2021/2/19 17:22
 * @Version 1.0
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}

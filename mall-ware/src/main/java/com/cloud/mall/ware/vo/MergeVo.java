package com.cloud.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/2/23 16:35
 * @Version 1.0
 */
@Data
public class MergeVo {
    /**
     * 整单id
     */
    private Long purchaseId;
    /**
     * /合并项集合
     */
    private List<Long> items;
}

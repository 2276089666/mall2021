package com.cloud.mall.product.vo;

import lombok.Data;

/**
 * @Author ws
 * @Date 2021/2/17 15:39
 * @Version 1.0
 */
@Data
public class AttrResponseVo extends AttrVo{
    /**
     * 所属分类的名字
     */
    private String catelogName;
    /**
     * 所属分组的名字
     */
    private String groupName;

    /**
     * 分类完整路径
     */
    private Long[] catelogPath;
}

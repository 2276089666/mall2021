package com.cloud.mall.product.vo;


import lombok.Data;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/2/18 18:54
 * @Version 1.0
 */
@Data
public class AttrGroupWithAttrVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 关联属性集合
     */
    List<AttrVo> attrs;
}

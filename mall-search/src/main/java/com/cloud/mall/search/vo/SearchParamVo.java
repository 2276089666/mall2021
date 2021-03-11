package com.cloud.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/11 19:42
 * @Version 1.0
 */

/**
 * 接收首页传过来的检索条件
 * keyword=小米&sort=saleCount_desc&hasStock=1&skuPrice=400_1900&brandId=1
 * &catalogId=1&attrs=1_3G:4G:5G&attrs=2_骁龙 845&attrs=4_高清屏
 */
@Data
public class SearchParamVo {
    /**
     * 搜索框的全文匹配关键字
     */
    private String keyword;

    /**
     * 页面传过来的三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件,三选一
     * sort=saleCount_desc  或者 sort=saleCount_asc
     * sort=hotScore_desc   或者 sort=hotScore_asc
     * sort=skuPrice_desc   或者 sort=skuPrice_asc
     */
    private String sort;

    /**
     * 是否有货
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     */
    private String skuPrice;

    /**
     * 品牌id,可以多个
     */
    private List <Long> brandId;

    /**
     * 按照属性
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNumber;
}

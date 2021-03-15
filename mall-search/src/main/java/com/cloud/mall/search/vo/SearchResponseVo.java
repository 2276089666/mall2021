package com.cloud.mall.search.vo;

import com.cloud.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/11 20:11
 * @Version 1.0
 */
@Data
public class SearchResponseVo {

    /**
     * 查询到所有商品的信息
     */
    private List<SkuEsModel> skuEsModelList;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 总页码
     */
    private Long total;

    /**
     * 有多少页的页码数组
     */
    private List<Integer> pageNavs;

    /**
     * 总页码
     */
    private  Integer totalPages;

    /**
     * 查询到的结果涉及到的所有的品牌相关数据
     */
    private List<BrandVo> brandVos;

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    /**
     * 查询到的结果涉及到的所有属性集合
     */
    private List<AttrVo> attrVos;

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List <String> attrValue;
    }

    /**
     * 查询到的结果的所有分类集合
     */
    private List<CatalogVo> catalogVos;

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}

package com.cloud.mall.product.vo;

import com.cloud.mall.product.entity.SkuImagesEntity;
import com.cloud.mall.product.entity.SkuInfoEntity;
import com.cloud.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/16 15:11
 * @Version 1.0
 */
@Data
public class ItemVo {

    // sku的基本信息 pms_sku_info
    private SkuInfoEntity skuInfoEntity;


    // sku的图片信息 pms_sku_images
    private List<SkuImagesEntity> skuImagesEntity;

    // spu的销售属性组合
    private List<SkuItemSaleAttr> saleAttrs;


    // 获取spu的介绍
    private SpuInfoDescEntity spuInfoDescEntity;

    // 获取spu的规格参数信息
    private List<SpuItemBaseAttr> groupAttrs;


}

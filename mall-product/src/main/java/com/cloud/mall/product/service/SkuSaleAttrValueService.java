package com.cloud.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.product.entity.SkuSaleAttrValueEntity;
import com.cloud.mall.product.vo.SkuItemSaleAttr;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttr> getSaleAttrsBySpuId(Long spuId);
}


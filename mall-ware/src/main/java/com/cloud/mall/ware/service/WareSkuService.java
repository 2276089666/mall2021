package com.cloud.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.to.SkuHasStockTo;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:38:35
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuHasStockTo> getSkuStock(List<Long> skuIds);
}


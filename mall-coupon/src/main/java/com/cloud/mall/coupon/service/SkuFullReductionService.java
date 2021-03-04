package com.cloud.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.to.SkuReductionTo;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:01:15
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SkuReductionTo skuReductionTo);
}


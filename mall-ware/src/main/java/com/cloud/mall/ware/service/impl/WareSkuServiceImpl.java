package com.cloud.mall.ware.service.impl;

import com.cloud.common.to.SkuHasStockTo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.ware.dao.WareSkuDao;
import com.cloud.mall.ware.entity.WareSkuEntity;
import com.cloud.mall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            wareSkuEntityQueryWrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)){
            wareSkuEntityQueryWrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockTo> getSkuStock(List<Long> skuIds) {
        return this.baseMapper.getStock(skuIds);
    }

}
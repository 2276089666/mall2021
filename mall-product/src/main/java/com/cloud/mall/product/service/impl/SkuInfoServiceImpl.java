package com.cloud.mall.product.service.impl;

import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cloud.mall.product.dao.SkuInfoDao;
import com.cloud.mall.product.entity.SkuInfoEntity;
import com.cloud.mall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> skuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            skuInfoEntityQueryWrapper.and(a->{
               a.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId)&&!catelogId.equalsIgnoreCase("0")){
            skuInfoEntityQueryWrapper.eq("catalog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)&&!brandId.equalsIgnoreCase("0")){
            skuInfoEntityQueryWrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)){
//            大于等于，如果是大于就用gt
            skuInfoEntityQueryWrapper.ge("price",min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)&&new BigDecimal(max).compareTo(new BigDecimal(0))>0){
            skuInfoEntityQueryWrapper.le("price",max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                skuInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));

        return list;
    }

}
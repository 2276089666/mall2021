package com.cloud.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.mall.product.dao.SkuInfoDao;
import com.cloud.mall.product.entity.SkuImagesEntity;
import com.cloud.mall.product.entity.SkuInfoEntity;
import com.cloud.mall.product.entity.SpuInfoDescEntity;
import com.cloud.mall.product.service.*;
import com.cloud.mall.product.vo.ItemVo;
import com.cloud.mall.product.vo.SkuItemSaleAttr;
import com.cloud.mall.product.vo.SpuItemBaseAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


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
        if (!StringUtils.isEmpty(key)) {
            skuInfoEntityQueryWrapper.and(a -> {
                a.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !catelogId.equalsIgnoreCase("0")) {
            skuInfoEntityQueryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !brandId.equalsIgnoreCase("0")) {
            skuInfoEntityQueryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
//            大于等于，如果是大于就用gt
            skuInfoEntityQueryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(new BigDecimal(0)) > 0) {
            skuInfoEntityQueryWrapper.le("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                skuInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        return list;
    }

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Override
    public ItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        ItemVo itemVo = new ItemVo();
        // 不需要等待任何人，但是需要返回值
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // sku的基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            if (skuInfoEntity != null) {
                itemVo.setSkuInfoEntity(skuInfoEntity);
            }
            return skuInfoEntity;
        }, threadPoolExecutor);

        // 要等待infoFuture的结果
        CompletableFuture<Void> saleAttrsFuture = infoFuture.thenAcceptAsync((result) -> {
            // spu的销售属性组合
            if (result != null) {
                Long spuId = result.getSpuId();
                List<SkuItemSaleAttr> saleAttrs = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
                if (saleAttrs != null && !saleAttrs.isEmpty()) {
                    itemVo.setSaleAttrs(saleAttrs);
                }
            }
        }, threadPoolExecutor);

        // 要等待infoFuture的结果
        CompletableFuture<Void> spuInfoDescFuture = infoFuture.thenAcceptAsync((result) -> {
            // 获取spu的介绍
            if (result != null) {
                Long spuId = result.getSpuId();
                SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
                if (spuInfoDescEntity != null) {
                    itemVo.setSpuInfoDescEntity(spuInfoDescEntity);
                }
            }
        }, threadPoolExecutor);

        // 要等待infoFuture的结果
        CompletableFuture<Void> groupAttrsFuture = infoFuture.thenAcceptAsync((result) -> {
            // 获取spu的规格参数信息
            if (result != null) {
                Long spuId = result.getSpuId();
                Long catalogId = result.getCatalogId();
                List<SpuItemBaseAttr> groupAttrs = attrGroupService.getAttrsAndAttrGroupBySpuId(catalogId, spuId);
                if (groupAttrs != null && !groupAttrs.isEmpty()) {
                    itemVo.setGroupAttrs(groupAttrs);
                }
            }
        }, threadPoolExecutor);

        // 不需要等待任何人
        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            // sku的图片信息 pms_sku_images
            List<SkuImagesEntity> skuImagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            if (skuImagesEntityList != null && !skuImagesEntityList.isEmpty()) {
                itemVo.setSkuImagesEntity(skuImagesEntityList);
            }
        }, threadPoolExecutor);

        /**
         * 阻塞等待所有任务都完成
         */
        //省略infoFuture，因为后面的三个任务都会依赖他
        CompletableFuture.allOf(saleAttrsFuture,spuInfoDescFuture,groupAttrsFuture,skuImagesFuture).get();

        return itemVo;
    }

}
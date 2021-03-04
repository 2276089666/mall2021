package com.cloud.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.constant.ProductConstant;
import com.cloud.common.to.SkuHasStockTo;
import com.cloud.common.to.SkuReductionTo;
import com.cloud.common.to.SpuBoundTo;
import com.cloud.common.to.es.SkuEsModel;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.common.utils.R;
import com.cloud.mall.product.dao.SpuInfoDao;
import com.cloud.mall.product.entity.*;
import com.cloud.mall.product.feign.CouponFeignService;
import com.cloud.mall.product.feign.SearchFeignService;
import com.cloud.mall.product.feign.WareFeignService;
import com.cloud.mall.product.service.*;
import com.cloud.mall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    //1.设置mysql的会话隔离级别方便我们调试我们大的保存函数 set session transaction isolation level read uncommitted
    //2.查询会话隔离级别和全局隔离级别 SELECT @@session.tx_isolation; SELECT @@global.tx_isolation;
    //3.使用查询语句查看我们认为变化的表
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1.保存spu的基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);
        //2.保存spu的描述图片 pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        if (decript != null && !decript.isEmpty()) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
            spuInfoDescEntity.setDecript(String.join(",", decript));
            spuInfoDescService.save(spuInfoDescEntity);
        }
        //3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        if (images != null && !images.isEmpty()) {
            List<SpuImagesEntity> collect = images.stream().map(a -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setSpuId(spuInfoEntity.getId());
                spuImagesEntity.setImgUrl(a);
                return spuImagesEntity;
            }).collect(Collectors.toList());
            spuImagesService.saveBatch(collect);
        }
        //4.保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        if (baseAttrs != null && !baseAttrs.isEmpty()) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(a -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrId(a.getAttrId());
                productAttrValueEntity.setAttrValue(a.getAttrValues());
                productAttrValueEntity.setQuickShow(a.getShowDesc());
                AttrEntity attrEntity = attrService.getById(a.getAttrId());
                if (attrEntity != null) {
                    productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                }
                productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }
        //5.保存spu的积分信息 跨数据库mall_sms ---> sms_spu_bounds

        Bounds bounds = spuSaveVo.getBounds();
        if (bounds != null) {
            SpuBoundTo spuBoundTo = new SpuBoundTo();
            BeanUtils.copyProperties(bounds, spuBoundTo);
            spuBoundTo.setSpuId(spuInfoEntity.getId());
            R r = couponFeignService.saveSpuBounds(spuBoundTo);
            if (r.getCode() != 0) {
                log.error("远程保存spu的积分信息失败");
            }
        }

        //6.保存当前spu对应的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && !skus.isEmpty()) {
            skus.forEach(a -> {
                //6.1.sku的基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(a, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                List<Images> list = a.getImages();
                String skuDefaultImg = "";
                if (list != null && !list.isEmpty()) {
                    for (Images image : list) {
                        if (image.getDefaultImg() == 1) {
                            skuDefaultImg = image.getImgUrl();
                        }
                    }
                }
                skuInfoEntity.setSkuDefaultImg(skuDefaultImg);
                skuInfoService.save(skuInfoEntity);

                //6.2.sku的图片信息 pms_sku_images
                List<Images> skuImages = a.getImages();
                if (skuImages != null && !skuImages.isEmpty()) {
                    List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(b -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                        skuImagesEntity.setDefaultImg(b.getDefaultImg());
                        skuImagesEntity.setImgUrl(b.getImgUrl());
                        return skuImagesEntity;
                    }).filter(e -> {
                        //过滤掉空的url
                        return StringUtils.isEmpty(e.getImgUrl());
                    }).collect(Collectors.toList());
                    skuImagesService.saveBatch(skuImagesEntities);
                }


                //6.3.sky的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = a.getAttr();
                if (attr != null && !attr.isEmpty()) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(c -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(c, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                }

                //6.4.sku的优惠和满减信息和会员价格 跨数据库mall_sms--->  sms_sku_ladder  sms_sku_full_reduction  sms_member_price
                if (a.getFullCount() > 0 || a.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                    SkuReductionTo skuReductionTo = new SkuReductionTo();
                    BeanUtils.copyProperties(skus, skuReductionTo);
                    skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0) {
                        log.error("远程保存sku的优惠和满减信息和会员价格失败");
                    }
                }
            });
        }


    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            spuInfoEntityQueryWrapper.and((a) -> {
                a.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            spuInfoEntityQueryWrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)) {
            spuInfoEntityQueryWrapper.eq("brand_id", brandId);
        }
        String catalogId = (String) params.get("catalogId");
        if (!StringUtils.isEmpty(catalogId)) {
            spuInfoEntityQueryWrapper.eq("catalog_id", catalogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public void up(Long spuId) {

        List<SkuEsModel> esModelList = new ArrayList<>();
        //查出当前的spuId对应的所有sku信息,品牌的名字
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuBySpuId(spuId);
        if (skuInfoEntities != null && !skuInfoEntities.isEmpty()) {
            List<SkuEsModel.Attrs> attrsList = null;
            //SkuEsModel里面的attrs不用放在遍历里面查询多次
            List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
            if (productAttrValueEntities != null && !productAttrValueEntities.isEmpty()) {
                List<Long> attrIds = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
                if (!attrIds.isEmpty()) {
                    List<AttrEntity> attrEntities = attrService.selectSearchAttrs(attrIds);
                    if (attrEntities != null && !attrEntities.isEmpty()) {
                        Set<Long> collect = attrEntities.stream().map(AttrEntity::getAttrId).collect(Collectors.toSet());
                        attrsList = productAttrValueEntities.stream().filter(c -> {
                            return collect.contains(c.getAttrId());
                        }).map(d -> {
                            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                            BeanUtils.copyProperties(d, attrs);
                            return attrs;
                        }).collect(Collectors.toList());
                    }
                }
            }

            List<SkuHasStockTo> data = null;
            try {
                //调用远程服务查看是否有库存
                List<Long> collect = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
                R hasStock = wareFeignService.getHasStock(collect);
                //不知道为什么Spring cloud fegin调用返回结果将SkuHasStockTo转换为LinkedHashMap问题
                data = hasStock.getData(new TypeReference<List<SkuHasStockTo>>() {
                });
            } catch (Exception e) {
                log.error("库存服务查询异常,原因{}", e);
            }
            Map<Long, Integer> hasStockMap = null;
            if (data != null && !data.isEmpty()) {
                //将数据转为map,方便我们下面的对象赋值
                hasStockMap = data.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getStock));
            }

            List<SkuEsModel.Attrs> finalAttrsList = attrsList;
            Map<Long, Integer> finalHasStockMap = hasStockMap;
            List<SkuEsModel> collect = skuInfoEntities.stream().map(a -> {
                //查出需要的数据,并封装成对象
                SkuEsModel skuEsModel = new SkuEsModel();
                BeanUtils.copyProperties(a, skuEsModel);
                skuEsModel.setSkuPrice(a.getPrice());
                skuEsModel.setSkuImg(a.getSkuDefaultImg());

                if (finalHasStockMap == null) {
                    skuEsModel.setHasStock(true);
                } else {
                    if (finalHasStockMap.get(a.getSkuId()) != null) {
                        skuEsModel.setHasStock(finalHasStockMap.get(a.getSkuId()) > 0);
                    } else {
                        skuEsModel.setHasStock(false);
                    }

                }

                // TODO: 2021/3/1 热度评分 0 先简单写着
                skuEsModel.setHotScore(0L);
                BrandEntity brandEntity = brandService.getById(a.getBrandId());
                if (brandEntity != null) {
                    skuEsModel.setBrandName(brandEntity.getName());
                    skuEsModel.setBrandImg(brandEntity.getLogo());
                }
                CategoryEntity categoryEntity = categoryService.getById(a.getCatalogId());
                if (categoryEntity != null) {
                    skuEsModel.setCatalogName(categoryEntity.getName());
                }
                skuEsModel.setAttrs(finalAttrsList);
                return skuEsModel;
            }).collect(Collectors.toList());

            R r = searchFeignService.productStatusUp(collect);
            if (r.getCode() == 0) {
                //es保存成功,修改spu的上架状态为上架
                this.baseMapper.updateSpuStatus(spuId, ProductConstant.ProductStatus.SPU_UP.getCode());
            } else {
//               调用失败
                // TODO: 2021/3/2 重复调用,接口幂等性问题,重试机制
                /**
                 * openfeign调用流程 SynchronousMethodHandler的invoke方法
                 * 1.构造请求数据,将对象转为json
                 * RequestTemplate template = this.buildTemplateFromArgs.create(argv);
                 * 2.发送请求进行执行(执行成功会解码响应数据)
                 * this.executeAndDecode(template, options);
                 * 3.执行请求会有重试机制
                 *       while(true) {
                 *             try {
                 *                 //执行请求
                 *                 return this.executeAndDecode(template, options);
                 *             } catch (RetryableException var9) {
                 *                 RetryableException e = var9;
                 *
                 *                 try {
                 *                      //重试器重试(有异常抛异常,没有继续重试)
                 *                     retryer.continueOrPropagate(e);
                 *                 } catch (RetryableException var8) {
                 *                     Throwable cause = var8.getCause();
                 *                     if (this.propagationPolicy == ExceptionPropagationPolicy.UNWRAP && cause != null) {
                 *                         throw cause;
                 *                     }
                 *
                 *                     throw var8;
                 *                 }
                 *                       //记录重试日志
                 *                 if (this.logLevel != Level.NONE) {
                 *                     this.logger.logRetry(this.metadata.configKey(), this.logLevel);
                 *                 }
                 *             }
                 *         }
                 */
            }
        }
    }

}
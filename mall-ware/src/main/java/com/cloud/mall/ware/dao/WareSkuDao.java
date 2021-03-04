package com.cloud.mall.ware.dao;

import com.cloud.common.to.SkuHasStockTo;
import com.cloud.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:38:35
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<SkuHasStockTo> getStock(@Param("skuIds") List<Long> skuIds);
}

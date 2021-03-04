package com.cloud.mall.product.dao;

import com.cloud.common.constant.ProductConstant;
import com.cloud.mall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:19
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("spuUp") ProductConstant.ProductStatus spuUp);
}

package com.cloud.mall.product.dao;

import com.cloud.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mall.product.vo.SpuItemBaseAttr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemBaseAttr> getAttrsAndAttrGroupBySpuId(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}

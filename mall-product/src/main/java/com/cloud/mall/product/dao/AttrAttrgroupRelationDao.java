package com.cloud.mall.product.dao;

import com.cloud.mall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mall.product.vo.AttrGroupRelationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteAll(@Param("list") List<AttrGroupRelationVo> list);
}

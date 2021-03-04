package com.cloud.mall.product.dao;

import com.cloud.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:19
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

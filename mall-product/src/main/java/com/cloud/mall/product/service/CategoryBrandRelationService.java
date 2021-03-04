package com.cloud.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:19
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    QueryWrapper<CategoryBrandRelationEntity> getlist(Long brandId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrandDetail(Long brandId, String name);

    void updateCategoryDetail(Long catId, String name);
}



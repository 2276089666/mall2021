package com.cloud.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.mall.product.dao.BrandDao;
import com.cloud.mall.product.dao.CategoryDao;
import com.cloud.mall.product.entity.BrandEntity;
import com.cloud.mall.product.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cloud.mall.product.dao.CategoryBrandRelationDao;
import com.cloud.mall.product.entity.CategoryBrandRelationEntity;
import com.cloud.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public QueryWrapper<CategoryBrandRelationEntity> getlist(Long brandId) {
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId);
        return queryWrapper;
    }

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        if (brandId>0&&catelogId>0){
            BrandEntity brandEntity = brandDao.selectById(brandId);
            CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
            if (brandEntity!=null){
                categoryBrandRelation.setBrandName(brandEntity.getName());
            }
            if (categoryEntity!=null){
                categoryBrandRelation.setCatelogName(categoryEntity.getName());
            }
        }
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrandDetail(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandName(name);
        this.update(categoryBrandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public void updateCategoryDetail(Long catId, String name) {
//        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
//        categoryBrandRelationEntity.setCatelogName(name);
//        this.update(categoryBrandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catId));
        /**
         * 使用mybatis来更新,与上面的可以二选一
         */
        this.baseMapper.updateCategory(catId,name);
    }

}
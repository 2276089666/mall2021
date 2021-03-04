package com.cloud.mall.product.service.impl;

import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.mall.product.entity.CategoryBrandRelationEntity;
import com.cloud.mall.product.service.CategoryBrandRelationService;
import com.cloud.mall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cloud.mall.product.dao.BrandDao;
import com.cloud.mall.product.entity.BrandEntity;
import com.cloud.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)){
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Transactional(rollbackFor = Exception.class)//级联修改加上事务
    @Override
    public void updatedetail(BrandEntity brand) {
        this.updateById(brand);
        //不仅要修改自己的这张表,还要修改有关联关系的表的冗余字段,保证冗余字段的一致性
        if (!StringUtils.isEmpty(brand.getName())){
            categoryBrandRelationService.updateBrandDetail(brand.getBrandId(),brand.getName());
        }

        // TODO: 2021/2/16  一旦修改表的数据记得修改关联表的冗余数据
    }



    @Override
    public List<BrandEntity> selectBrand(Long catId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        /**
         * 判断集合一定要判断他是否为空，和size
         */
        if (categoryBrandRelationEntities!=null&&!categoryBrandRelationEntities.isEmpty()){
            List<BrandEntity> collect = categoryBrandRelationEntities.stream().map(a -> {
                Long brandId = a.getBrandId();
                return baseMapper.selectById(brandId);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

}
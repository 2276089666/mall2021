package com.cloud.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.constant.ProductConstant;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.mall.product.dao.AttrAttrgroupRelationDao;
import com.cloud.mall.product.dao.AttrDao;
import com.cloud.mall.product.dao.AttrGroupDao;
import com.cloud.mall.product.dao.CategoryDao;
import com.cloud.mall.product.entity.AttrAttrgroupRelationEntity;
import com.cloud.mall.product.entity.AttrEntity;
import com.cloud.mall.product.entity.AttrGroupEntity;
import com.cloud.mall.product.entity.CategoryEntity;
import com.cloud.mall.product.service.AttrAttrgroupRelationService;
import com.cloud.mall.product.service.AttrService;
import com.cloud.mall.product.service.CategoryService;
import com.cloud.mall.product.vo.AttrResponseVo;
import com.cloud.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        //保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);

        //如果是基本属性
        if (attrVo.getAttrType() == ProductConstant.AttributeType.ATTR_TYPE_BASE.getCode() && attrVo.getAttrGroupId()!=null) {
            //保存关联关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPages(Map<String, Object> params, Long catelogId, String type) {
        //看url的路径,来判断是销售属性还是基本属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttributeType.ATTR_TYPE_BASE.getCode() : ProductConstant.AttributeType.ATTR_TYPE_SALE.getCode());
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            attrEntityQueryWrapper.and((a) -> {
                a.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        if (catelogId != 0) {
            attrEntityQueryWrapper.eq("catelog_id", catelogId);
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVo> attrResponses = records.stream().map((attrEntity) -> {
            AttrResponseVo attrResponse = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, attrResponse);

            if ("base".equalsIgnoreCase(type)) {
                //所属分组名字赋值
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrResponse.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //所属分类赋值
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrResponse.setCatelogName(categoryEntity.getName());
            }
            return attrResponse;
        }).collect(Collectors.toList());
        pageUtils.setList(attrResponses);
        return pageUtils;
    }

    @Override
    public List<AttrEntity> getAttrs(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        if (!attrAttrgroupRelationEntities.isEmpty()) {
            List<Long> attrs = attrAttrgroupRelationEntities.stream().map((a) -> {
                return a.getAttrId();
            }).collect(Collectors.toList());
            List<AttrEntity> list = this.listByIds(attrs);
            return list;
        }
        return null;
    }

    @Autowired
    AttrDao attrDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = attrDao.selectById(attrId);
        AttrResponseVo attrResponse = new AttrResponseVo();
        if (attrEntity != null) {
            BeanUtils.copyProperties(attrEntity, attrResponse);
            Long catelogId = attrEntity.getCatelogId();
            Long[] path = categoryService.findCateLogPath(catelogId);
            attrResponse.setCatelogPath(path);
            CategoryEntity categoryEntity = categoryService.getById(catelogId);
            if (categoryEntity != null) {
                attrResponse.setCatelogName(categoryEntity.getName());
            }
        }
        //如果是基本属性,我们要查出所属分组的名字
        if (attrEntity.getAttrType() == ProductConstant.AttributeType.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (attrAttrgroupRelationEntity != null) {
                attrResponse.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrResponse.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        return attrResponse;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);

        //如果是基本类型,我们要修改和分组有关的信息
        if (attrEntity.getAttrType() == ProductConstant.AttributeType.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId());
            Integer integer = attrAttrgroupRelationDao.selectCount(attrAttrgroupRelationEntityQueryWrapper);
            if (integer > 0) {
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, attrAttrgroupRelationEntityQueryWrapper);
            } else {
                attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    /**
     * 获取当前分组没有关联的的所有属性
     *
     * @param params      分页参数
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttrs(Map<String, Object> params, Long attrgroupId) {

        //当分组只能关联自己所属分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();


        //只能关联别的分组没有关联的属性,自己已经关联的属性也要除外

        //查询所有分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>());
        List<Long> attrGroupIds = attrGroupEntities.stream().map((a) -> {
            return a.getAttrGroupId();
        }).collect(Collectors.toList());
        //找到别的分组关联的实体,以及自己的关联实体
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(a -> {
            return a.getAttrId();
        }).collect(Collectors.toList());

        //当分组只能关联自己所属分类里面的所有属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttributeType.ATTR_TYPE_BASE.getCode());
        if (!attrIds.isEmpty()) {
            //找到别的分组关联属性以外的属性
            attrEntityQueryWrapper.notIn("attr_id", attrIds);
        }
        //加上模糊查询功能
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            attrEntityQueryWrapper.and(a -> {
                a.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;

    }

    /**
     * 删除基本属性的同时还要删除关联
     * @param asList
     */
    @Override
    public void removeAttrAndRelationByIds(List<Long> asList) {
        this.removeByIds(asList);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_id", asList));
        if (attrAttrgroupRelationEntities!=null&&!attrAttrgroupRelationEntities.isEmpty()){
            attrAttrgroupRelationDao.deleteBatchIds(attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getId).collect(Collectors.toList()));
        }
    }

    @Override
    public List<AttrEntity> selectSearchAttrs(List<Long> attrIds) {
        List<AttrEntity> attrEntities = this.baseMapper.selectList(new QueryWrapper<AttrEntity>().eq("search_type", 1).in("attr_id", attrIds));
        return attrEntities;
    }


}
package com.cloud.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;
import com.cloud.mall.product.dao.AttrGroupDao;
import com.cloud.mall.product.entity.AttrEntity;
import com.cloud.mall.product.entity.AttrGroupEntity;
import com.cloud.mall.product.service.AttrGroupService;
import com.cloud.mall.product.service.AttrService;
import com.cloud.mall.product.service.CategoryService;
import com.cloud.mall.product.vo.AttrGroupWithAttrVo;
import com.cloud.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPages(Map<String, Object> params, Long cateLogId) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        //模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((a) -> {
                a.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        //通过id查
        if (cateLogId != 0) {
            queryWrapper.eq("catelog_id", cateLogId);
        }

        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);

    }

    @Autowired
    AttrService attrService;

    /**
     * 根据分类id查出所有的分组以及分组的所有属性
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrVo> getAttrAndGroupByCatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrVo> attrGroupWithAttrVos = attrGroupEntities.stream().map(a -> {
            AttrGroupWithAttrVo attrGroupWithAttrVo = new AttrGroupWithAttrVo();
            BeanUtils.copyProperties(a, attrGroupWithAttrVo);
            List<AttrEntity> attrs = attrService.getAttrs(a.getAttrGroupId());
            if (attrs != null && !attrs.isEmpty()) {
                List<AttrVo> attrVos = attrs.stream().map(b -> {
                    AttrVo attrVo = new AttrVo();
                    BeanUtils.copyProperties(b, attrVo);
                    attrVo.setAttrGroupId(a.getAttrGroupId());
                    return attrVo;
                }).collect(Collectors.toList());
                attrGroupWithAttrVo.setAttrs(attrVos);
            }
            return attrGroupWithAttrVo;
        }).collect(Collectors.toList());
        return attrGroupWithAttrVos;
    }


}
package com.cloud.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.cloud.common.utils.PageUtils;
import com.cloud.mall.product.entity.AttrGroupEntity;
import com.cloud.mall.product.vo.AttrGroupWithAttrVo;
import com.cloud.mall.product.vo.ItemVo;
import com.cloud.mall.product.vo.SpuItemBaseAttr;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPages(Map<String, Object> params, Long cateLogId);

    List<AttrGroupWithAttrVo> getAttrAndGroupByCatelogId(Long catelogId);

    List<SpuItemBaseAttr> getAttrsAndAttrGroupBySpuId(Long catalogId, Long spuId);
}


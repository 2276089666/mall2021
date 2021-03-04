package com.cloud.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.product.entity.AttrEntity;
import com.cloud.mall.product.vo.AttrResponseVo;
import com.cloud.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryPages(Map<String, Object> params, Long catelogId, String type);

    List<AttrEntity> getAttrs(Long attrgroupId);

    AttrResponseVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    PageUtils getNoRelationAttrs(Map<String, Object> params, Long attrgroupId);

    void removeAttrAndRelationByIds(List<Long> asList);

    List<AttrEntity> selectSearchAttrs(List<Long> attrIds);
}


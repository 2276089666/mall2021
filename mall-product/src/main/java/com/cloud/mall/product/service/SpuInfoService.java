package com.cloud.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.product.entity.SpuInfoEntity;
import com.cloud.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:19
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);
}


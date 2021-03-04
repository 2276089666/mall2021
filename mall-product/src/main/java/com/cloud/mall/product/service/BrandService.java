package com.cloud.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.product.entity.BrandEntity;
import com.cloud.mall.product.vo.BrandVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author chenshun
 * @email 2276089666@qq.com
 * @date 2021-01-09 13:23:20
 */
@Component
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updatedetail(BrandEntity brand);

    List<BrandEntity> selectBrand(Long catId);
}


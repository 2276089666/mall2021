package com.cloud.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.ware.entity.PurchaseDetailEntity;
import com.cloud.mall.ware.vo.MergeVo;

import java.util.Map;

/**
 * 
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:38:36
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

}


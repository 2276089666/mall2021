package com.cloud.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.ware.entity.PurchaseEntity;
import com.cloud.mall.ware.vo.MergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:38:36
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivePage(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);
}


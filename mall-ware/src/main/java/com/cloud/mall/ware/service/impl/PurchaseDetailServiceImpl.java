package com.cloud.mall.ware.service.impl;

import com.cloud.common.constant.WareConstant;
import com.cloud.mall.ware.entity.PurchaseEntity;
import com.cloud.mall.ware.vo.MergeVo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.ware.dao.PurchaseDetailDao;
import com.cloud.mall.ware.entity.PurchaseDetailEntity;
import com.cloud.mall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            purchaseDetailEntityQueryWrapper.and(a->{
               a.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            purchaseDetailEntityQueryWrapper.eq("status",status);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(key)){
            purchaseDetailEntityQueryWrapper.eq("ware_id",wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                purchaseDetailEntityQueryWrapper
        );

        return new PageUtils(page);
    }


}
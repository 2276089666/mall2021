package com.cloud.mall.ware.service.impl;

import com.cloud.common.constant.WareConstant;
import com.cloud.mall.ware.entity.PurchaseDetailEntity;
import com.cloud.mall.ware.service.PurchaseDetailService;
import com.cloud.mall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.ware.dao.PurchaseDao;
import com.cloud.mall.ware.entity.PurchaseEntity;
import com.cloud.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceivePage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId=purchaseEntity.getId();
        }
        PurchaseDetailEntity purchaseDetailServiceById = purchaseDetailService.getById(purchaseId);
        if (purchaseDetailServiceById!=null){
            //只有状态新建或已分配的采购单才能重新分配
            if (purchaseDetailServiceById.getStatus()==WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()||purchaseDetailServiceById.getStatus()==WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()){
                List<Long> items = mergeVo.getItems();
                if (items!=null&&!items.isEmpty()){
                    Long finalPurchaseId = purchaseId;
                    List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(a -> {
                        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                        purchaseDetailEntity.setId(a);
                        purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                        return purchaseDetailEntity;
                    }).collect(Collectors.toList());
                    purchaseDetailService.updateBatchById(purchaseDetailEntities);
                }
            }
        }
    }

}
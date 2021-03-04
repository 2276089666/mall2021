package com.cloud.mall.coupon.service.impl;

import com.cloud.common.to.MemberPrice;
import com.cloud.common.to.SkuReductionTo;
import com.cloud.mall.coupon.entity.MemberPriceEntity;
import com.cloud.mall.coupon.entity.SkuLadderEntity;
import com.cloud.mall.coupon.service.MemberPriceService;
import com.cloud.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.coupon.dao.SkuFullReductionDao;
import com.cloud.mall.coupon.entity.SkuFullReductionEntity;
import com.cloud.mall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    MemberPriceService memberPriceService;


    @Override
    public void saveInfo(SkuReductionTo skuReductionTo) {
        if (skuReductionTo.getFullCount()>0){
            //满几件打几折
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }


        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))>0){
            //满减
            SkuFullReductionEntity skuFullReductionEntity=new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
            this.save(skuFullReductionEntity);
        }

        //会员价格
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if (memberPrice!=null&&!memberPrice.isEmpty()){
            List<MemberPriceEntity> memberPriceEntityList = memberPrice.stream().map(a -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberLevelId(a.getId());
                memberPriceEntity.setMemberLevelName(a.getName());
                memberPriceEntity.setMemberPrice(a.getPrice());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            }).filter(b->{
                return b.getMemberPrice().compareTo(new BigDecimal(0))>0;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(memberPriceEntityList);
        }
    }

}